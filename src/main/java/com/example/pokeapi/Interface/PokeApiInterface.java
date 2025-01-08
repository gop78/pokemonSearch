package com.example.pokeapi.Interface;

import com.example.pokeapi.model.PokemonInfo;
import com.example.pokeapi.model.PokemonKoNames;
import com.example.pokeapi.propertie.ApiProperites;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class PokeApiInterface {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PokeApiInterface(WebClient.Builder webClientBuilder, ObjectMapper objectMapper, ApiProperites apiProperites) {

        // webClient 버퍼 크기 10MB로 확장
        ExchangeStrategies exchangeStrategies = ExchangeStrategies
                .builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                .build();

        String url = apiProperites.getUrl();
        this.webClient = webClientBuilder.baseUrl(url).exchangeStrategies(exchangeStrategies).build();
        this.objectMapper = objectMapper;
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
                        response -> response.bodyToMono(String.class).map(Exception::new)
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

    /**
     * JSON -> OBJECT로 변환 후 imageUrl 셋팅
     * @param json
     * @return
     */
    private Mono<PokemonInfo> convertJsonToPokemonInfo(String json) {
        try {
            PokemonInfo pokemonInfo = objectMapper.readValue(json, PokemonInfo.class);
            pokemonInfo.setImageUrl(pokemonInfo.getSprites().getFront_default());
            return Mono.just(pokemonInfo);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    /**
     * pokeAPI 인터페이스 후 JSON -> OBJECT 형변환
     * @param url
     * @param val
     * @return
     */
    public Mono<PokemonInfo> fetchPokemonInfo(String url, String val) {
        return webClientInterface(url, val)
                .flatMap(this::convertJsonToPokemonInfo);
    }

    public Mono<PokemonInfo> fetchPokemonInfo(String url) {
        return fetchPokemonInfo(url, null);
    }

    /**
     * JSON -> OBJECT로 변환 후 imageUrl 셋팅
     * @param json
     * @return
     */
    private Mono<PokemonKoNames> convertJsonToPokemonKoNames(String json) {
        try {
            PokemonKoNames pokemonInfo = objectMapper.readValue(json, PokemonKoNames.class);
            return Mono.just(pokemonInfo);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    /**
     * pokeAPI 인터페이스 후 JSON -> OBJECT 형변환
     * @param url
     * @param val
     * @return
     */
    public Mono<PokemonKoNames> fetchPokemonKoNames(String url, String val) {
        return webClientInterface(url, val)
                .flatMap(this::convertJsonToPokemonKoNames);
    }

    public Mono<PokemonKoNames> fetchPokemonKoNames(String url) {
        return fetchPokemonKoNames(url, null);
    }
}
