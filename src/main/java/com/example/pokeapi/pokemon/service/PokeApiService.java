package com.example.pokeapi.pokemon.service;

import com.example.pokeapi.pokemon.model.PokemonInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface PokeApiService {

    Mono<PokemonInfo> getPokemonInfoByName(String pokemonName);

    /* 포켓몬 목록 조회 */
    List<PokemonInfo> getPokemonList();

}