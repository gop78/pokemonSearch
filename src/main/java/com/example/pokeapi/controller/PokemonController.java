package com.example.pokeapi.controller;

import com.example.pokeapi.model.PokemonInfo;
import com.example.pokeapi.service.PokeApiService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
@Log4j2
public class PokemonController {

    private final PokeApiService pokeApiService;

    public PokemonController(PokeApiService pokeApiService) {
        this.pokeApiService = pokeApiService;
    }

    /**
     * 검색 페이지
     * @param model
     * @param startIndex
     * @param limitIndex
     * @return
     */
    @GetMapping
    public String list(Model model, @RequestParam(value = "startIndex", defaultValue = "1") int startIndex,
                       @RequestParam(value = "limitIndex", defaultValue = "150") int limitIndex) {
        List<PokemonInfo> pokemonInfoList =  pokeApiService.getPokemonList();
        model.addAttribute("pokemonInfoList", pokemonInfoList);
        return "search";
    }

    /**
     * 검색 페이지
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/pokemon")
    public String searchPokemon(@RequestParam String name, Model model) {

        PokemonInfo pokemonInfo = pokeApiService.getPokemonInfoByName(name.toLowerCase().trim()).onErrorComplete().block();
        if (Objects.isNull(pokemonInfo)) {
            model.addAttribute("error", "Not Found " + name);
            return "pokemon";
        }

        model.addAttribute("pokemonInfo", pokemonInfo);
        return "pokemon";
    }

    /**
     * 인덱스 검색 목록
     * @param model
     * @return
     */
    @GetMapping("/pokemonByIndex")
    public String pokemonByIndex(Model model) {
        List<PokemonInfo> pokemonInfoList =  pokeApiService.getPokemonList();
        model.addAttribute("pokemonInfoList", pokemonInfoList);
        return "pokemonList";
    }

    /**
     * 로그인 페이지
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
}




