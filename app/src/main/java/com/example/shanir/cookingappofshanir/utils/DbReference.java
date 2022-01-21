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

    public static DatabaseReference getDbRefToRecipe(String recipeName) {
        String recipePath = General.RECIPE_TABLE_NAME + "/" + recipeName;
        return FirebaseDatabase.getInstance().getReference(recipePath);
    }

    public static DatabaseReference getDbRefToUserFavoriteRecipes(String userID) {
        String userFavoriteRecipesTablePath = General.FAVORITE_RECIPES +
                "/" + userID + "/" + General.RECIPE_FAVORITE_NAMES;
        return FirebaseDatabase.getInstance()
                .getReference(userFavoriteRecipesTablePath);
    }

    public static StorageReference getDbRefToRecipeBitmap(String bitmap) {
        return FirebaseStorage.getInstance().
                getReferenceFromUrl(General.RECIPE_IMAGES_URL).child(bitmap);
    }

    public static StorageReference getDbRefToImageProfileBitmap(String bitmap) {
        return FirebaseStorage.getInstance().getReference()
                .child(General.PROFILE_IMAGES_URL).child(bitmap);
    }

    public static DatabaseReference getDbRefToUser(String userID) {
        return FirebaseDatabase.getInstance()
                .getReference(General.USER_TABLE_NAME).child(userID);
    }
}