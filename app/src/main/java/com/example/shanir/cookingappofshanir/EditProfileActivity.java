package com.example.shanir.cookingappofshanir;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.DbConstants;
import com.example.shanir.cookingappofshanir.utils.ImageUtilities;
import com.example.shanir.cookingappofshanir.utils.Permission;
import com.example.shanir.cookingappofshanir.utils.ProgressBarManager;
import com.example.shanir.cookingappofshanir.utils.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class EditProfileActivity extends ImagePromptActivity {

    private EditText mFirstNameEditText, mLastNameEditText, mIdEditText,
            mPhoneEditText;
    private Button mUpdateProfileButton;
    private TextView mEditProfileImageTextView;
    private FirebaseAuth mFireBaseAuth;
    private ImageView mExitImageView;
    private ProgressBar mProgressBar;
    private boolean mImageHasChanged = false;
    private HashMap<String, Object> userDetailsMap;
    private ProgressBarManager progressBarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mFirstNameEditText = findViewById(R.id.et_firstname_update_profile);
        mLastNameEditText = findViewById(R.id.et_lastname_update_profile);
        mPhoneEditText = findViewById(R.id.et_phone_update_profile);
        mIdEditText = findViewById(R.id.et_id_update_profile);
        mImageView = findViewById(R.id.iv_update_profile_image);
        mProgressBar = findViewById(R.id.pb_update_profile);

        mFireBaseAuth = FirebaseAuth.getInstance();
        progressBarManager = new ProgressBarManager(findViewById(R.id.edit_profile_progressbar));
        userDetailsMap = new HashMap<>();

        Intent i = getIntent();
        if (i.getExtras() != null) {
            mBitmapName = i.getExtras().getString("bitmap");
        }

        initUpdateProfileButton();
        initEditProfileImageTextView();
        initExitImageView();
    }

    private void initUpdateProfileButton() {
        mUpdateProfileButton = findViewById(R.id.bt_update_profile);
        mUpdateProfileButton.setOnClickListener(v -> {
            progressBarManager.requestVisible();
            editProfile();
        });
    }

    private void initEditProfileImageTextView() {
        mEditProfileImageTextView = findViewById(R.id.tv_edit_profile_image);
        mEditProfileImageTextView.setOnClickListener(v -> {
            Permission permission = new Permission(
                    EditProfileActivity.this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
        });
    }

    private void initExitImageView() {
        mExitImageView = findViewById(R.id.close_image_view);
        mExitImageView.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfileActivity.this,
                    UserProfileActivity.class);
            startActivity(intent);
        });
    }

    private void editProfile() {
        String firstname = mFirstNameEditText.getText().toString();
        String lastname = mLastNameEditText.getText().toString();
        String phone = mPhoneEditText.getText().toString();
        String id = mIdEditText.getText().toString();


        DbReference.getDbRefToUser(mFireBaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userDetailsMap.put("firstname", firstname);
                userDetailsMap.put("lastname", lastname);
                userDetailsMap.put("id", id);
                userDetailsMap.put("phone", phone);
                uploadImage(DbConstants.PROFILE_IMAGES_URL);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void uploadImage(String directoryImage) {
        if (mImageUri == null)
            return;

        mBitmapName = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        DbReference.getDbRefToImageBitmap(directoryImage, mBitmapName)
                .putFile(mImageUri)
                .addOnSuccessListener(
                        ((OnSuccessListener<UploadTask.TaskSnapshot>) taskSnapshot -> {
                            Toast.makeText(EditProfileActivity.this,
                                    "Image Uploaded",
                                    Toast.LENGTH_SHORT).show();

                            userDetailsMap.put("bitmap", mBitmapName);
                            DbReference.getDbRefToUser(
                                    mFireBaseAuth.getUid()).updateChildren(userDetailsMap)
                                    .addOnSuccessListener(unused -> {
                                        progressBarManager.requestGone();
                                        finish();
                                    });
                        }))
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to upload image",
                                Toast.LENGTH_SHORT).show());
    }


    @Override
    protected void onStart() {
        super.onStart();
        DbReference.getDbRefToUser(mFireBaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);

                if (userprofile == null)
                    return;

                mFirstNameEditText.setText(userprofile.getFirstname());
                mLastNameEditText.setText(userprofile.getLastname());
                mIdEditText.setText(userprofile.getId());
                mPhoneEditText.setText(userprofile.getPhone());

                if (!userprofile.getBitmap().equals("none") && !mImageHasChanged)
                    ImageUtilities.loadImage(
                            DbConstants.APP_PROFILE_IMAGES_FULL_URL +
                                        userprofile.getBitmap(),
                                        mImageView, mProgressBar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onGalleryResult(@Nullable Intent data)
            throws IOException, NullPointerException {
        super.onGalleryResult(data);
        mImageHasChanged = true;
    }

    @Override
    protected void onCameraResult(@Nullable Intent data)
            throws NullPointerException {
        super.onCameraResult(data);
        mImageHasChanged = true;
    }
}