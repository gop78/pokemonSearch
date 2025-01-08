package com.example.pokeapi.model;

import lombok.Data;

import java.util.List;

@Data
public class PokemonKoNames {

    private List<Name> names;

    @Data
    public static class Language {
        private String name;
    }

    @Data
    public static class Name {
        private String name;
        private Language language;
    }
}
