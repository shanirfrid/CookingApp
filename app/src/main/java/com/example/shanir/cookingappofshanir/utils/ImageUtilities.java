package com.example.shanir.cookingappofshanir.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

public class ImageUtilities {

    public static void loadImage(String directory, String bitmapId, ImageView loadToImageView,
                                 @Nullable ProgressBar progressBar) {
        DbReference.getDbFullRefToImageBitmap(directory, bitmapId)
                .getBytes(General.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            progressBar.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            loadToImageView.setImageBitmap(bitmap);
        });
    }

}
