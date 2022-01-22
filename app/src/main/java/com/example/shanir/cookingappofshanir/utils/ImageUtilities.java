package com.example.shanir.cookingappofshanir.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

public class ImageUtilities {

    public static void loadImage(String pathToBitmap, ImageView loadToImageView,
                                 @Nullable ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        DbReference.getDbFullRefToImageBitmap(pathToBitmap)
                .getBytes(General.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            progressBar.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            loadToImageView.setImageBitmap(bitmap);
        });
    }

}
