package com.example.shanir.cookingappofshanir.classs;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.cert.CRL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shanir on 24/03/2018.
 */

public class ListofIngredientsadmin implements Parcelable {
    ArrayList<Ingredients> ingredients;

    public ListofIngredientsadmin() {

        this.ingredients = new ArrayList<Ingredients>();

    }

    public void addIngredient(Ingredients ingredient) {

        ingredients.add(ingredient);
    }

    public ArrayList<Ingredients> getlist() {
        return this.ingredients;
    }

    public void showIngredients() {
        for (Ingredients ingredientI : ingredients) {
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




