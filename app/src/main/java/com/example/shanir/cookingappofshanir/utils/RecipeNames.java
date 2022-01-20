package com.example.shanir.cookingappofshanir.utils;

import java.util.ArrayList;

public class RecipeNames {
    private ArrayList<String> mRecipeNames;

    public RecipeNames() {
        this.mRecipeNames = new ArrayList<>();
    }

    public RecipeNames(ArrayList<String> recipeNames){
        this.mRecipeNames = recipeNames;
    }

    public void setRecipeNames(ArrayList<String> recipeNames)
    {
        this.mRecipeNames = recipeNames;
    }

    public void add(String recipeName) {
        mRecipeNames.add(recipeName);
    }

    public ArrayList<String> getRecipeNames(){
        return this.mRecipeNames;
    }
}
