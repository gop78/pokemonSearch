package com.example.pokeapi.pokemon.service.serviceImpl;

import com.example.pokeapi.pokemon.client.PokeApiInterface;
import com.example.pokeapi.pokemon.model.PokemonInfo;
import com.example.pokeapi.pokemon.model.PokemonKoNames;
import com.example.pokeapi.pokemon.service.PokeApiService;
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

        // API 호출을 위한 URL 생성
        List<String> urls = new ArrayList<>();
        List<String> krNmUrls = new ArrayList<>();
        for (int i = offset; i <= limit ; i++) {
            urls.add(String.format("/pokemon/%d", i));
            krNmUrls.add(String.format("/pokemon-species/%d", i));
        }

        // 포켓몬 상제 정보 조회
        List<PokemonInfo> pokemonInfoList = getPokemonInfoList(urls);
        if (pokemonInfoList.isEmpty()) {
            log.error("pokemonInfoList is empty");
            return new ArrayList<>();
        }

        // 한글 목록 조회
        List<PokemonKoNames> pokemonKoNamesList = getPokemonKoNamesList(krNmUrls);
        if (pokemonKoNamesList.isEmpty()) {
            log.error("pokemonKoNamesList is empty");
            return new ArrayList<>();
        }

        // 한글 이름으로 변경
        pokemonKoNameMerge(pokemonInfoList, pokemonKoNamesList);

        return pokemonInfoList;
    }

    /**
     * 포켓몬 상세 정보 조회
     * @param urls
     * @return
     */
    private List<PokemonInfo> getPokemonInfoList(List<String> urls) {
        return Flux.fromIterable(urls)
                .flatMap(pokeApiInterface::fetchPokemonInfo)
                .sort(Comparator.comparingInt(PokemonInfo::getId))
                .collectList()
                .block();

    }

    /**
     * 포켓몬 한글 이름 조회
     * @param urls
     * @return
     */
    private List<PokemonKoNames> getPokemonKoNamesList(List<String> urls) {
        return Flux.fromIterable(urls)
                .flatMap(pokeApiInterface::fetchPokemonKoNames)
                .collectList()
                .block();
    }

    private void pokemonKoNameMerge(List<PokemonInfo> pokemonInfoList, List<PokemonKoNames> pokemonKoNamesList) {
        pokemonKoNamesList.forEach(pokemonKoNames -> {
            // 한국어 이름 추출
            String koName = pokemonKoNames.getNames().stream()
                    .filter(language -> "ko".equals(language.getLanguage().getName()))
                    .map(PokemonKoNames.Name::getName)
                    .findFirst()
                    .orElse("");

            // pokemonInfo 리스트 순회하여 이름 변경
            pokemonInfoList.forEach(pokemonInfo -> pokemonKoNames.getNames().stream()
                    .filter(language -> "en".equals(language.getLanguage().getName()))
                    .filter(language -> pokemonInfo.getName().equalsIgnoreCase(language.getName()))
                    .findFirst()
                    .ifPresent(language -> pokemonInfo.setName(koName))
            );
        });

    }
}
