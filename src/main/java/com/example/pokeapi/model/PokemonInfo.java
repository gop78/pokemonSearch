package com.example.pokeapi.model;

import lombok.Data;

import java.util.List;

@Data
public class PokemonInfo {

    private String name;
    private List<PokemonType> types;
    private PokemonSprites sprites;
    private String imageUrl; // Add this field
    private int height;
    private int weight;
    private String heightUnit = "cm";
    private String weightUnit = "kg";

    private int setHeight(int height) {
        return this.height = height * 10;
    }

    private int setWeight(int weight) {
        return this.weight = weight / 10;
    }

    @Data
    public static class PokemonType {
        private PokemonTypeDetail type;
    }

    @Data
    public static class PokemonTypeDetail {
        private String name;
    }


}


