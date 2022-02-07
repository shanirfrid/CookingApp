package com.example.shanir.cookingappofshanir.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Recipe {
    private ArrayList<Ingredient> list;
    private String bitmap;
    private Bitmap nameBitmap;
    private String nameOfrecipe;
    private String difficulty;
    private int time;
    String tID;

    public Recipe() {
        list = new ArrayList<>();
        bitmap = "none";
    }

    public void setNameBitmap(Bitmap nameBitmap) {
        this.nameBitmap = nameBitmap;
    }

    public Bitmap getNameBitmap() {
        return this.nameBitmap;
    }

    public ArrayList<Ingredient> getIngredientList() {
        return list;
    }

    public void setIngredientList(ArrayList<Ingredient> list) {
        this.list = list;
    }

    public String gettID() {
        return tID;
    }

    public String getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getNameOfrecipe() {
        return nameOfrecipe;
    }

    public void setNameOfrecipe(String nameOfrecipe) {
        this.nameOfrecipe = nameOfrecipe;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "שם המתכון: " + nameOfrecipe + "\n" + "קושי: " + difficulty + "\n" +
                "זמן מתכון: " + time;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Recipe)) {
            return false;
        }
        return this.getNameOfrecipe().equals(((Recipe) o).getNameOfrecipe());
    }
}
