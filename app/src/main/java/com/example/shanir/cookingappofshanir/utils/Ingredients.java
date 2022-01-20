package com.example.shanir.cookingappofshanir.utils;

/**
 * Created by Shanir on 03/03/2018.
 */

public class Ingredients {
    private String name;
    private String units;

    public Ingredients( String name, String units) {
        this.name = name;
        this.units = units;
    }

    public Ingredients()
    {}

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
