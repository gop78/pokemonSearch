package com.example.pokeapi.service;

import com.example.pokeapi.model.PokemonInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
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

    private final int indexLimit = 151;

    public PokeApiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://pokeapi.co/api/v2").build();
        this.objectMapper = objectMapper;
    }

    /**
     * 이름으로 조회
     *
     * @param pokemonName
     * @return
     */
    public Mono<PokemonInfo> getPokemonInfoByName(String pokemonName) {
        return webClient.get()
                .uri("/pokemon/{name}", pokemonName)
                .retrieve()
                .bodyToFlux(DataBuffer.class) // Flux<DataBuffer> 형태로 데이터를 받아온다.
                .reduce(DataBuffer::write)    // 모든 DataBuffer를 하나의 버퍼로 결합
                .map(buffer -> {              // DataBuffer를 JSON 문자열로 변환
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .flatMap(json -> {
                    try {
                        PokemonInfo pokemonInfo = objectMapper.readValue(json, PokemonInfo.class);
                        // front_default 스프라이트를 기반으로 imageUrl을 설정
                        pokemonInfo.setImageUrl(pokemonInfo.getSprites().getFront_default());
                        return Mono.just(pokemonInfo);
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                });
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

            webClient.get()
                    .uri("/pokemon/{index}", i)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class) // Flux<DataBuffer> 형태로 데이터를 받아온다.
                    .reduce(DataBuffer::write)    // 모든 DataBuffer를 하나의 버퍼로 결합
                    .map(buffer -> {              // DataBuffer를 JSON 문자열로 변환
                        byte[] bytes = new byte[buffer.readableByteCount()];
                        buffer.read(bytes);
                        DataBufferUtils.release(buffer);
                        return new String(bytes, StandardCharsets.UTF_8);
                    })
                    .flatMap(json -> {
                        try {
                            PokemonInfo pokemonInfo = objectMapper.readValue(json, PokemonInfo.class);
                            // front_default 스프라이트를 기반으로 imageUrl을 설정
                            pokemonInfo.setImageUrl(pokemonInfo.getSprites().getFront_default());
                            pokemonInfoList.add(pokemonInfo);
                            return Mono.just(pokemonInfo);
                        } catch (JsonProcessingException e) {
                            return Mono.error(e);
                        }
                    })
                    .block();
        }

        return pokemonInfoList;
    }
}




