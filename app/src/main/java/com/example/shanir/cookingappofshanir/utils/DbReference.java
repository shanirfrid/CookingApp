package com.example.shanir.cookingappofshanir.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DbReference {

    public static DatabaseReference getDbRefToUserIngredients(String userID) {
        String userIngredientsTablePath = General.USER_INGREDIENTS_TABLE_NAME +
                "/" + userID + "/" + General.USER_INGREDIENTS_SUB_TABLE_NAME;
        return FirebaseDatabase.getInstance()
                .getReference(userIngredientsTablePath);
    }

    public static DatabaseReference getDbRefToRecipes() {
        return FirebaseDatabase.getInstance()
                .getReference(General.RECIPE_TABLE_NAME);
    }

    public static StorageReference getDbRefToRecipeImages() {
        return FirebaseStorage.getInstance().
                getReferenceFromUrl(General.RECIPE_IMAGES_URL);
    }
}
