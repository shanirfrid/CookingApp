package com.example.shanir.cookingappofshanir.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.example.shanir.cookingappofshanir.utils.db.DbConstants;
import com.example.shanir.cookingappofshanir.utils.db.DbReference;

public class ImageUtilities {

    public static void loadImage(String pathToBitmap, ImageView loadToImageView,
                                 @Nullable ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        DbReference.getDbFullRefToImageBitmap(pathToBitmap)
                .getBytes(DbConstants.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            progressBar.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            loadToImageView.setImageBitmap(bitmap);
        });
    }

}
