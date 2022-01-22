package com.example.shanir.cookingappofshanir;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.FileHelper;
import com.example.shanir.cookingappofshanir.utils.General;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class ImagePromptActivity extends AppCompatActivity {
    public static final int GALLERY_REQUEST_CODE = 1, CAMERA_REQUEST_CODE = 2;
    protected ImageView mImageView;
    protected Uri mImageUri;
    protected String mBitmapName;
    protected String mImageFileNameCamera = General.PROFILE_IMAGE_FILE_NAME_CAMERA;
    protected ProgressBar mProgressBar;

    protected void loadImage(String bitmapId, String fullDirectoryPath) {
        mProgressBar.setVisibility(View.VISIBLE);
        DbReference.getDbFullRefToImageBitmap(fullDirectoryPath, bitmapId)
                .getBytes(General.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            mProgressBar.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mImageView.setImageBitmap(bitmap);
        });
    }

    protected void uploadImage(String directoryImage) {
        if (mImageUri == null)
            return;

        mBitmapName = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        DbReference.getDbRefToImageBitmap(directoryImage, mBitmapName)
                .putFile(mImageUri).addOnSuccessListener(taskSnapshot ->
                Toast.makeText(this,
                        "Image Uploaded",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to upload image",
                                Toast.LENGTH_SHORT).show());
    }

    protected void showPictureDialog() {
        AlertDialog.Builder profileImageDialogBuilder =
                new AlertDialog.Builder(this);

        profileImageDialogBuilder.setTitle("Add Photo!");
        String[] pictureDialogItems = {"Select photo from gallery", "Take Photo"};
        profileImageDialogBuilder.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            selectPhotoFromGallery();
                            break;
                        case 1:
                            selectPhotoFromCamera();
                            break;
                    }
                });
        profileImageDialogBuilder.show();
    }

    protected void selectPhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    protected void selectPhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    protected void onGalleryResult(@Nullable Intent data) throws IOException, NullPointerException {
        mImageUri = data.getData();
        Bitmap bitmapGallery = MediaStore.Images.Media
                .getBitmap(this.getContentResolver(), mImageUri);
        mImageView.setImageBitmap(bitmapGallery);
    }

    protected void onCameraResult(@Nullable Intent data) throws NullPointerException {
        Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
        mImageView.setImageBitmap(cameraBitmap);
        FileHelper.saveBitmapToFile(cameraBitmap, this,
                mImageFileNameCamera);
        File tmpFile = new File(getFilesDir() + "/" +
                mImageFileNameCamera);
        mImageUri = Uri.fromFile(tmpFile);
    }

//    protected  void loadImage(String name){
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    this.onGalleryResult(data);
                    break;
                case CAMERA_REQUEST_CODE:
                    this.onCameraResult(data);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
