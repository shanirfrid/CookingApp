package com.example.shanir.cookingappofshanir.activities.user;

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

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.db.DbReference;
import com.example.shanir.cookingappofshanir.utils.db.DbConstants;
import com.example.shanir.cookingappofshanir.utils.ImageUtilities;
import com.example.shanir.cookingappofshanir.utils.Permission;
import com.example.shanir.cookingappofshanir.utils.ProgressBarManager;
import com.example.shanir.cookingappofshanir.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class EditProfileActivity extends ImagePromptActivity {

    private EditText mFirstNameEditText, mLastNameEditText, mIdEditText,
            mPhoneEditText;
    private Button mSaveButton;
    private TextView mEditProfileImageTextView;
    private FirebaseAuth mFireBaseAuth;
    private ImageView mExitImageView;
    private ProgressBar mProfilePictureProgressBar;
    private boolean mImageHasChanged = false;
    private HashMap<String, Object> userDetailsMap;
    private ProgressBarManager mSaveProgressBarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);
        mFirstNameEditText = findViewById(R.id.edit_profile_first_name_edit_text);
        mLastNameEditText = findViewById(R.id.edit_profile_last_name_edit_text);
        mPhoneEditText = findViewById(R.id.edit_profile_phone_edit_text);
        mIdEditText = findViewById(R.id.edit_profile_id_edit_text);
        mImageView = findViewById(R.id.edit_profile_picture_image_view);
        mProfilePictureProgressBar = findViewById(R.id.edit_profile_photo_progress_bar);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mSaveProgressBarManager = new ProgressBarManager(findViewById(R.id.edit_profile_save_progress_bar));
        userDetailsMap = new HashMap<>();

        Intent i = getIntent();
        if (i.getExtras() != null) {
            mBitmapName = i.getExtras().getString("bitmap");
        }

        initSaveButton();
        initEditProfileImageTextView();
        initExitImageView();
    }

    private void initSaveButton() {
        mSaveButton = findViewById(R.id.edit_profile_save_image_view);
        mSaveButton.setOnClickListener(v -> {
            mSaveProgressBarManager.requestVisible();
            saveUserDetails();
        });
    }

    private void initEditProfileImageTextView() {
        mEditProfileImageTextView = findViewById(R.id.edit_profile_change_picture_text_view);
        mEditProfileImageTextView.setOnClickListener(v -> {
            Permission permission = new Permission(
                    EditProfileActivity.this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
        });
    }

    private void initExitImageView() {
        mExitImageView = findViewById(R.id.edit_profile_discard_image_view);
        mExitImageView.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfileActivity.this,
                    UserProfileActivity.class);
            startActivity(intent);
        });
    }

    private void saveUserDetails() {
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

                if (mImageUri != null)
                    uploadImage(DbConstants.PROFILE_IMAGES_URL);
                else
                    uploadUserDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void uploadUserDetails() {
        DbReference.getDbRefToUser(
                mFireBaseAuth.getUid()).updateChildren(userDetailsMap)
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this,
                            "Error occurred trying to save update profile...",
                            Toast.LENGTH_SHORT);
                })
                .addOnCompleteListener(task -> {
                    mSaveProgressBarManager.requestGone();
                    finish();
                });
    }

    @Override
    protected void uploadImage(String directoryImage) {
        if (mImageUri == null)
            return;

        mBitmapName = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        DbReference.getDbRefToImageBitmap(directoryImage, mBitmapName)
                .putFile(mImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    userDetailsMap.put("bitmap", mBitmapName);
                    uploadUserDetails();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to upload image",
                                Toast.LENGTH_SHORT).show()
                );
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
                            mImageView, mProfilePictureProgressBar);
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