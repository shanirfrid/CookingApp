package com.example.shanir.cookingappofshanir.classs;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Shanir on 21/04/2018.
 */

public class FileHelper {
    public static String saveBitmapToFile(Bitmap bitmap, Context
            context, String fileName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // write the compressed bitmap to the outputStream(bytes)
        bitmap.compress(Bitmap.CompressFormat.PNG ,100, bytes);
        FileOutputStream fo = null;
        try {
            fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "בעיה ,",Toast.LENGTH_SHORT).show();
        }
        try {
            fo.write(bytes.toByteArray());
            fo.close();// close file output
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}

