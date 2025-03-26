package com.example.pokeapi.pokemon.controller;

import com.example.pokeapi.pokemon.model.PokemonInfo;
import com.example.pokeapi.pokemon.service.PokeApiService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/pokemon")
public class PokemonController {

    private final PokeApiService pokeApiService;

    public PokemonController(PokeApiService pokeApiService) {
        this.pokeApiService = pokeApiService;
    }

    /**
     * 포켓몬 목록 페이지
     * @return
     */
    @GetMapping
    public List<PokemonInfo> getPokemonList() {
        return pokeApiService.getPokemonList();
    }

    /**
     * 검색 페이지
     * @param name
     * @return
     */
    @GetMapping("/{name}")
    public PokemonInfo getPokemonInfoByName(@PathVariable String name) {
        return pokeApiService.getPokemonInfoByName(name.toLowerCase().trim()).onErrorComplete().block();
    }

    /**
     * 인덱스 검색 목록
     * @return
     */
    @GetMapping("/pokemonByIndex")
    public List<PokemonInfo> pokemonByIndex() {
        return pokeApiService.getPokemonList();
    }
}




