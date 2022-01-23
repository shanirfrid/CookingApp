package com.example.shanir.cookingappofshanir.utils;

public class Ingredient {
    private String name;
    private String units;

    public Ingredient(String name, String units) {
        this.name = name;
        this.units = units;
    }

    public Ingredient() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
