package com.example.pokeapi.controller;

import com.example.pokeapi.model.PokemonInfo;
import com.example.pokeapi.service.PokeApiService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Controller
@Log4j2
public class PokemonController {

    private final PokeApiService pokeApiService;

    public PokemonController(PokeApiService pokeApiService) {
        this.pokeApiService = pokeApiService;
    }

    @GetMapping("/")
    public String showSearchPage(Model model, @RequestParam(defaultValue = "1") int startIndex, @RequestParam(defaultValue = "10") int limitIndex) {
        List<PokemonInfo> pokemonInfoList =  pokeApiService.pokemonByIndex(startIndex, limitIndex);
        model.addAttribute("pokemonInfoList", pokemonInfoList);
        return "search";
    }

    @GetMapping("/pokemon")
    public String searchPokemon(@RequestParam String name, Model model) {
        PokemonInfo pokemonInfo = null;
        try {
            pokemonInfo = pokeApiService.getPokemonInfoByName(name.toLowerCase().trim()).block();
        } catch (WebClientResponseException e) {
            log.error(e);
            model.addAttribute("error", "Not Found " + name);
            return "pokemon";
        }

        model.addAttribute("pokemonInfo", pokemonInfo);
        return "pokemon";
    }

    @GetMapping("/pokemonByIndex")
    public String pokemonByIndex(Model model, @RequestParam int startIndex, @RequestParam int limitIndex) {
        List<PokemonInfo> pokemonInfoList =  pokeApiService.pokemonByIndex(startIndex, limitIndex);
        model.addAttribute("pokemonInfoList", pokemonInfoList);
        return "pokemonList";
    }
}




