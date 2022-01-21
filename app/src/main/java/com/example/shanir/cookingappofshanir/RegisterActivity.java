package com.example.shanir.cookingappofshanir;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.General;
import com.example.shanir.cookingappofshanir.utils.FileHelper;
import com.example.shanir.cookingappofshanir.utils.Permission;
import com.example.shanir.cookingappofshanir.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private EditText mPasswordEditText, mFirstNameEditText, mEmailEditText,
            mLastNameEmail, mPhoneEditText, mIdEditText, mConfirmPassEditText;
    private TextView mBackTextView;
    private Button mSaveButton;
    private String mBitmapName;
    private ScrollView mScrollView;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFireBaseAuth;
    private ImageView mProfileImageView;
    private Uri mUriProfileImage;
    private int GALLERY_REQUEST_CODE = 1, CAMERA_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mPasswordEditText = findViewById(R.id.etpasswardregister);
        mConfirmPassEditText = findViewById(R.id.etconfirmPassword);
        mFirstNameEditText = findViewById(R.id.etfirstname);
        mEmailEditText = findViewById(R.id.etEmail);
        mIdEditText = findViewById(R.id.etid);
        mPhoneEditText = findViewById(R.id.etPhone);
        mLastNameEmail = findViewById(R.id.etlastname);
        mProgressBar = findViewById(R.id.progressbarregister);

        mFireBaseAuth = FirebaseAuth.getInstance();
        initSaveButton();
        initProfileImageView();
        initBackTextView();
        initScrollView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initScrollView() {
        mScrollView = findViewById(R.id.sv_signUp);
        mScrollView.setOnTouchListener((view, motionEvent) -> {
            mEmailEditText.clearFocus();
            mConfirmPassEditText.clearFocus();
            mPasswordEditText.clearFocus();
            return false;
        });
    }

    private void initBackTextView() {
        mBackTextView = findViewById(R.id.tv_back);
        mBackTextView.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    private void initSaveButton() {
        mSaveButton = findViewById(R.id.btSaveregister);
        mSaveButton.setOnClickListener(v -> registerUser());
    }

    private void initProfileImageView() {
        mProfileImageView = findViewById(R.id.iv_account_profile);
        mProfileImageView.setOnClickListener(v -> {
            Permission permission = new Permission(RegisterActivity.this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
        });
    }

    private void registerUser() {
        String password = mPasswordEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();
        String comfirmPassword = mConfirmPassEditText.getText().toString().trim();

        if (!comfirmPassword.equals(password)) {
            mPasswordEditText.setError("Passwords do not match");
            mPasswordEditText.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            mEmailEditText.setError("Email required");
            mEmailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailEditText.setError("Please enter a valid email");
            mEmailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            mPasswordEditText.setError("Password required");
            mPasswordEditText.requestFocus();
            return;
        }
        if (password.length() < General.MINIMAL_PASSWORD_SIZE) {
            mPasswordEditText.setError("Passwords should be a minimum of 6 characters in length ");
            mPasswordEditText.requestFocus();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mFireBaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    mProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        if (mUriProfileImage != null) {
                            mBitmapName = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
                            uploadImage(mUriProfileImage);
                        }
                        adduser();
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            Toast.makeText(RegisterActivity.this, "OOPS! you are already signed in",
                                    Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void adduser() {
        String firstname = mFirstNameEditText.getText().toString();
        String lastname = mLastNameEmail.getText().toString();
        String pass = mPasswordEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String phone = mPhoneEditText.getText().toString();
        String id = mIdEditText.getText().toString();
        User user;

        if (mBitmapName == null)
            user = new User(firstname, lastname, id, pass, phone, email, null, "none");
        else
            user = new User(firstname, lastname, id, pass, phone, email, null, mBitmapName);

        DbReference.getDbRefToUser(mFireBaseAuth.getUid()).setValue(user, (databaseError, databaseReferfinence) -> {
            if (databaseError == null) {
                Toast.makeText(RegisterActivity.this, "data successfully saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), UserIngredientsActivity.class);
                startActivity(intent);
                finish();
            } else
                Toast.makeText(RegisterActivity.this,
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showPictureDialog() {
        AlertDialog.Builder profileImageDialogBuilder = new AlertDialog.Builder(this);

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


    private void uploadImage(Uri imageUri) {
        DbReference.getDbRefToImageProfileBitmap(mBitmapName)
                .putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                Toast.makeText(RegisterActivity.this,
                        "Image Uploaded",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(RegisterActivity.this,
                                "Failed to upload image",
                                Toast.LENGTH_SHORT).show());
    }

    public void selectPhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void selectPhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (data == null)
                return;
            mUriProfileImage = data.getData();
            try {
                Bitmap bitmapGallery = MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(), mUriProfileImage);
                mProfileImageView.setImageBitmap(bitmapGallery);
            } catch (IOException e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap bitmapCamera = (Bitmap) data.getExtras().get("data");
            mProfileImageView.setImageBitmap(bitmapCamera);
            FileHelper.saveBitmapToFile(bitmapCamera,
                    RegisterActivity.this, General.PROFILE_IMAGE_FILE_NAME);
            File tmpFile = new File(getFilesDir() + "/" +
                    General.PROFILE_IMAGE_FILE_NAME);
            mUriProfileImage = Uri.fromFile(tmpFile);
        }
    }
}









