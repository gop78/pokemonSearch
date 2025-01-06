package com.example.pokeapi.service.serviceImpl;

import com.example.pokeapi.Interface.PokeApiInterface;
import com.example.pokeapi.model.PokemonInfo;
import com.example.pokeapi.service.PokeApiService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Log4j2
public class PokeApiServiceImpl implements PokeApiService {
    private final PokeApiInterface pokeApiInterface;

    public PokeApiServiceImpl(PokeApiInterface pokeApiInterface) {
        this.pokeApiInterface = pokeApiInterface;
    }

    @Value("${api.offset}")
    private int offset;

    @Value("${api.limit}")
    private int limit;


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
     * 포켓몬 목록 조회
     * @return
     */
    public List<PokemonInfo> getPokemonList() {

        List<String> urls = new ArrayList<String>();
        for (int i = offset; i <= limit ; i++) {
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
