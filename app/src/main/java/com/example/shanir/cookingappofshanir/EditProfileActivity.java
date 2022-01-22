package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.General;
import com.example.shanir.cookingappofshanir.utils.Permission;
import com.example.shanir.cookingappofshanir.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;


public class EditProfileActivity extends ImagePromptActivity {

    private EditText mFirstNameEditText, mLastNameEditText, mIdEditText,
            mPhoneEditText;
    private Button mUpdateProfileButton;
    private TextView mEditProfileImageTextView;
    private FirebaseAuth mFireBaseAuth;
    private ImageView mExitImageView;
    private boolean mImageHasChanged = false;

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
            editProfile();
            Intent intent = new Intent(EditProfileActivity.this,
                    UserProfileActivity.class);
            startActivity(intent);
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
        HashMap<String, Object> userDetailsMap = new HashMap<>();

        DbReference.getDbRefToUser(mFireBaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userDetailsMap.put("firstname", firstname);
                userDetailsMap.put("lastname", lastname);
                userDetailsMap.put("id", id);
                userDetailsMap.put("phone", phone);
                uploadImage(General.PROFILE_IMAGES_URL);
                userDetailsMap.put("bitmap", mBitmapName);
                DbReference.getDbRefToUser(
                        mFireBaseAuth.getUid()).updateChildren(userDetailsMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
                    loadImage(userprofile.getBitmap(),
                            General.APP_IMAGES_FULL_URL + General.PROFILE_IMAGES_URL);
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