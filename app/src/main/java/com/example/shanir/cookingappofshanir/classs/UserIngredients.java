package com.example.shanir.cookingappofshanir.classs;

import java.util.ArrayList;

/**
 * Created by gul on 25/03/2018.
 */

public class UserIngredients {
    private ArrayList<String> ingredients = new ArrayList<>();

    public UserIngredients() {
    }


    public void add(ArrayList<String> list) {
        ingredients = new ArrayList<>();
        for (String s:list)
            ingredients.add(s);
    }

    public void add(String name)
    {
        ingredients.add(name);
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}
