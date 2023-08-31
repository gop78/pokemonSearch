package com.example.pokeapi.service;

import com.example.pokeapi.model.PokemonInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Service
public class PokeApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PokeApiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://pokeapi.co/api/v2").build();
        this.objectMapper = objectMapper;
    }

    public Mono<PokemonInfo> getPokemonInfo(String pokemonName) {
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
}




