package com.example.shanir.cookingappofshanir.classs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shanir on 03/03/2018.
 */

public class Recipe  {
    private ArrayList<Ingredients> list;

    public void setNameBitmap(Bitmap nameBitmap) {
        this.nameBitmap = nameBitmap;
    }

    private String bitmap;
    private Bitmap nameBitmap;
    private String kindOfrecipe;
    private String nameOfrecipe;
    private String difficulty;
    private int time;
    String tID;
    String key;

    public  Recipe()
    {
        list= new ArrayList<>();
        bitmap = "none";
    }
    public Recipe(String nameOfrecipe,String kindOfrecipe,String difficulty,int time)
    {

        this.kindOfrecipe = kindOfrecipe;
        this.nameOfrecipe = nameOfrecipe;
        this.difficulty = difficulty;
        this.time = time;

    }


    public Recipe( ArrayList<Ingredients> list,
                   String  bitmap, String kindOfrecipe, String nameOfrecipe, String difficulty, int time) {

        this.list = list;
        this.bitmap = bitmap;
        this.kindOfrecipe = kindOfrecipe;
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


    public String getBitmap() {
        return this.bitmap;
    }

    public Bitmap getNameBitmap(){
        return this.nameBitmap;
    }



    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public String getKindOfrecipe() {
        return kindOfrecipe;
    }

    public void setKindOfrecipe(String kindOfrecipe) {
        this.kindOfrecipe = kindOfrecipe;
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
        return  "שם המתכון: " +nameOfrecipe +"\n" +"סוג המתכון: " +kindOfrecipe+ "\n" +"קושי: "+difficulty+"\n"+
                "זמן מתכון: "+time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
