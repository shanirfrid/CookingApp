package com.example.shanir.cookingappofshanir.utils;

public class TextFormatter {

    public static String formatRecipeTime(int minutes) {
        if (minutes < 60)
            return String.format("%dm", minutes);

        return String.format("%dh %02dm", minutes / 60, minutes % 60);
    }

    public static String foundRecipesNumber(int recipesAmount)
    {
        switch (recipesAmount) {
            case 0:
                return "No recipe was found!";
            case 1:
                return "1 recipe was found!";
            default:
                return recipesAmount + " recipes were found!";
        }
    }
}
