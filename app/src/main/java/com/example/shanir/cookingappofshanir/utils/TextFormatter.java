package com.example.shanir.cookingappofshanir.utils;

public class TextFormatter {

    public static String formatRecipeTime(int minutes) {
        if (minutes < 60)
            return String.format("%dm", minutes);

        return String.format("%dh %02dm", minutes / 60, minutes % 60);
    }
}
