package com.example.shanir.cookingappofshanir.utils;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class ListofIngredientsadmin implements Parcelable {
    ArrayList<Ingredient> ingredients;

    public ListofIngredientsadmin() {
        this.ingredients = new ArrayList<Ingredient>();
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public ArrayList<Ingredient> getlist() {
        return this.ingredients;
    }

    public void showIngredients() {
        for (Ingredient ingredientI : ingredients) {
            System.out.println(ingredientI.getName() + " ");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Out.writeInt(ingredients);

    }

    public static final Parcelable.Creator<ListofIngredientsadmin> CREATOR = new Parcelable.Creator<ListofIngredientsadmin>() {
        public ListofIngredientsadmin createFromParcel(Parcel in) {
            return new ListofIngredientsadmin();
        }

        public ListofIngredientsadmin[] newArray(int size) {
            return new ListofIngredientsadmin[size];
        }
    };
}




