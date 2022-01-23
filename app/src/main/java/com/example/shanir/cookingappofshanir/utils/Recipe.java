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
    String key;

    public Recipe() {
        list = new ArrayList<>();
        bitmap = "none";
    }

    public Recipe(ArrayList<Ingredient> list,
                  String bitmap, String nameOfrecipe, String difficulty, int time) {

        this.list = list;
        this.bitmap = bitmap;
        this.nameOfrecipe = nameOfrecipe;
        this.difficulty = difficulty;
        this.time = time;
        this.list = new ArrayList<>();
    }


    public ArrayList<String> getIngredients() {
        ArrayList<String> namelist = new ArrayList<String>();
        String nameIngredient;
        for (int i = 0; i < this.list.size(); i++) {
            nameIngredient = this.list.get(i).getName().trim();
            namelist.add(nameIngredient);
        }
        return namelist;
    }

    public void setNameBitmap(Bitmap nameBitmap) {
        this.nameBitmap = nameBitmap;
    }

    public Bitmap getNameBitmap() {
        return this.nameBitmap;
    }

    public ArrayList<Ingredient> getList() {
        return list;
    }

    public void setList(ArrayList<Ingredient> list) {
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

    public void addingredienttolist(Ingredient ingredient) {
        this.list.add(ingredient);
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
