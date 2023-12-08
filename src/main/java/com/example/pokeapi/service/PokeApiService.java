package com.example.pokeapi.service;

import com.example.pokeapi.Interface.PokeApiInterface;
import com.example.pokeapi.model.PokemonInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Log4j2
public class PokeApiService {

    @Autowired
    private PokeApiInterface pokeApiInterface;

    /**
     * 이름으로 포켓몬 조회
     *
     * @param pokemonName
     * @return
     */
    public Mono<PokemonInfo> getPokemonInfoByName(String pokemonName) {

        return pokeApiInterface.fetchPokemonInfo("/pokemon/{name}", pokemonName);
    }

    /**
     * index번호로 포켓몬 조회
     *
     * @param startIndex
     * @param limitItem
     * @return
     */
    public List<PokemonInfo> pokemonByIndex(int startIndex, int limitItem) {

        List<String> urls = new ArrayList<String>();
        for (int i = startIndex; i <= limitItem ; i++) {
            urls.add(String.format("/pokemon/%d", i));
        }

        List<PokemonInfo> pokemonInfoList = Flux.fromIterable(urls)
                .flatMap(url -> pokeApiInterface.fetchPokemonInfo(url))
                .sort(Comparator.comparingInt(PokemonInfo::getId))
                .collectList()
                .block();

        return pokemonInfoList;
    }
}