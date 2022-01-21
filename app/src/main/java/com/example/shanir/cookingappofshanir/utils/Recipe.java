package com.example.shanir.cookingappofshanir.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Shanir on 03/03/2018.
 */

public class Recipe  {
    private ArrayList<Ingredients> list;

    public void setBitmapName(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private String bitmapName;
    private Bitmap bitmap;
    private String nameOfrecipe;
    private String difficulty;
    private int time;
    String tID;
    String key;

    public  Recipe()
    {
        list= new ArrayList<>();
        bitmapName = "none";
    }
    public Recipe(String nameOfrecipe,String difficulty,int time)
    {

        this.nameOfrecipe = nameOfrecipe;
        this.difficulty = difficulty;
        this.time = time;

    }


    public Recipe( ArrayList<Ingredients> list,
                   String  bitmap, String nameOfrecipe, String difficulty, int time) {

        this.list = list;
        this.bitmapName = bitmap;
        this.nameOfrecipe = nameOfrecipe;
        this.difficulty = difficulty;
        this.time = time;
        this.list= new ArrayList<>();
    }

    public Recipe( ArrayList<Ingredients> list,
                   Bitmap  bitmap, String nameOfrecipe, String difficulty, int time) {

        this.list = list;
        this.bitmap = bitmap;
        this.nameOfrecipe = nameOfrecipe;
        this.difficulty = difficulty;
        this.time = time;
        this.list= new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public ArrayList<Ingredients> getList() {
        return list;
    }

    public ArrayList<String> getListNameIngredientOnRecipe()
    {
        ArrayList<String> namelist=new ArrayList<String>();
        String nameIngredient;
        for (int i=0;i<this.list.size();i++)
        {
            nameIngredient=this.list.get(i).getName().trim();
            namelist.add(nameIngredient);
        }
        return namelist;

    }
    public boolean HasIngredients(String name)
    {
        ArrayList<Ingredients> listi=getList();
        for (int i=0;i<listi.size();i++)
        {
           if (listi.get(i).getName().equals(name))
               return true;
        }
        return  false;


    }


    public void setList(ArrayList<Ingredients> list) {
        this.list = list;
    }

    public String gettID() {
        return tID;
    }

    public void settID(String tID) {
        this.tID = tID;
    }

    public void addingredienttolist(Ingredients ingredients)
    {
        this.list.add(ingredients);
    }


    public String getBitmapName() {
        return this.bitmapName;
    }

    public Bitmap getBitmap(){
        return this.bitmap;
    }



    public void setBitmapName(String bitmapName) {
        this.bitmapName = bitmapName;
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

    @Override
    public String toString() {
        return  "שם המתכון: " +nameOfrecipe +"\n" +"קושי: "+difficulty+"\n"+
                "זמן מתכון: "+time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Recipe)){
            return false;
        }
        return this.getNameOfrecipe().equals(((Recipe) o).getNameOfrecipe());

    }
}
