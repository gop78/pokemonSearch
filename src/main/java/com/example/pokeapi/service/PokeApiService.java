package com.example.pokeapi.service;

import com.example.pokeapi.model.PokemonInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class PokeApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("api.url")
    private String url;

    private final int indexLimit = 151;

    public PokeApiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl(url).build();
        this.objectMapper = objectMapper;
    }

    /**
     * 이름으로 조회
     *
     * @param pokemonName
     * @return
     */
    public Mono<PokemonInfo> getPokemonInfoByName(String pokemonName) {

       return webClientInterface("/pokemon/{name}", pokemonName).flatMap(
               json -> {
                   try {
                       PokemonInfo pokemonInfo = objectMapper.readValue(json, PokemonInfo.class);
                       // front_default 스프라이트를 기반으로 imageUrl을 설정
                       pokemonInfo.setImageUrl(pokemonInfo.getSprites().getFront_default());
                       return Mono.just(pokemonInfo);
                   } catch (JsonProcessingException e) {
                       return Mono.error(e);
                   }
               }
       );
    }

    /**
     * 포켓몬 index 조회
     * @return
     */
    public List<PokemonInfo> pokemonByIndex(int startIndex, int limitItem) {

        List<PokemonInfo> pokemonInfoList  = new ArrayList<PokemonInfo>();
        for (int i = startIndex; i <= limitItem ; i++) {

            if (i > indexLimit) {
                break;
            }

            webClientInterface("/pokemon/{index}", String.valueOf(i)).flatMap(
                            json -> {
                                try {
                                    PokemonInfo pokemonInfo = objectMapper.readValue(json, PokemonInfo.class);
                                    // front_default 스프라이트를 기반으로 imageUrl을 설정
                                    pokemonInfo.setImageUrl(pokemonInfo.getSprites().getFront_default());
                                    pokemonInfoList.add(pokemonInfo);
                                    return Mono.just(pokemonInfo);
                                } catch (JsonProcessingException e) {
                                    return Mono.error(e);
                                }
                            }
                    ).block();
        }

        return pokemonInfoList;
    }

    /**
     * 포켓몬 api 인터페이스
     * @param url
     * @param val
     * @return
     */
    public Mono<String> webClientInterface(String url, String val) {
        return webClient.get()
                .uri(url, val)
                .retrieve()
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        response ->
                                response.bodyToMono(String.class).map(Exception::new)
                )
                .bodyToFlux(DataBuffer.class) // Flux<DataBuffer> 형태로 데이터를 받아온다.
                .reduce(DataBuffer::write)    // 모든 DataBuffer를 하나의 버퍼로 결합
                .map(buffer -> {              // DataBuffer를 JSON 문자열로 변환
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                });
    }
}




