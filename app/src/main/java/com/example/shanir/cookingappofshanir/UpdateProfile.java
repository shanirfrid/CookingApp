package com.example.shanir.cookingappofshanir;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.utils.FileHelper;
import com.example.shanir.cookingappofshanir.utils.General;
import com.example.shanir.cookingappofshanir.utils.Permission;
import com.example.shanir.cookingappofshanir.utils.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class UpdateProfile extends ImagePromptActivity implements View.OnClickListener {

    EditText mFirstName, mLastName, mId, mPhone;
    Button button;
    TextView tvEditProfileImage;
    String tableusers;
    FirebaseAuth mAuth;
    ImageView mExit;
    final String PIC_FILE_NAME_PROFIL = "userpicprofil";
    ProgressBar mPb;
    boolean imageHasChanged = false;
    private DatabaseReference postRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        button = (Button) findViewById(R.id.bt_update_profile);
        mFirstName = (EditText) findViewById(R.id.et_firstname_update_profile);
        mLastName = (EditText) findViewById(R.id.et_lastname_update_profile);
        mPhone = (EditText) findViewById(R.id.et_phone_update_profile);
        mId = (EditText) findViewById(R.id.et_id_update_profile);
        mImageView = (ImageView) findViewById(R.id.iv_update_profile_image);
        mPb = (ProgressBar) findViewById(R.id.pb_update_profile);
        mExit = (ImageView) findViewById(R.id.close_image_view);
        tvEditProfileImage = (TextView) findViewById(R.id.tv_edit_profile_image);

        Intent i = getIntent();
        if (i.getExtras() != null) {
            mBitmapName = i.getExtras().getString("bitmap");
        }
        mAuth = FirebaseAuth.getInstance();
        button.setOnClickListener(this);
        tvEditProfileImage.setOnClickListener(this);
        mExit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            updateProfile();
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        } else if (v == tvEditProfileImage) {
            Permission permission = new Permission(this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
        } else if (v == mExit) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setRefToTables();
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofil = snapshot.getValue(User.class);
                userprofil.setIdkey(mAuth.getCurrentUser().getUid());
                if (userprofil.getIdkey().equals(mAuth.getCurrentUser().getUid())) {
                    mFirstName.setText(userprofil.getFirstname());
                    mLastName.setText(userprofil.getLastname());
                    mId.setText(userprofil.getId());
                    mPhone.setText(userprofil.getPhone());

                    if (!userprofil.getBitmap().equals("none") && !imageHasChanged)
                        loadImage(userprofil.getBitmap());
                }

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
        imageHasChanged = true;
    }

    @Override
    protected void onCameraResult(@Nullable Intent data)
            throws NullPointerException {
        super.onCameraResult(data);
        imageHasChanged = true;
    }

    private void loadImage(String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://cookingappofshanir.appspot.com/imagesprofil/").child(name);
        final long ONE_MEGABYTE = 1024 * 1024 * 5;

        //download file as a byte array
        mPb.setVisibility(View.VISIBLE);
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(byte[] bytes) {
                mPb.setVisibility(View.GONE);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mImageView.setImageBitmap(bitmap);
            }
        });
    }

    private void setRefToTables() {
        String uid = mAuth.getCurrentUser().getUid();
        tableusers = General.USER_TABLE_NAME + "/" + uid;
        postRef = FirebaseDatabase.getInstance().getReference(tableusers);
    }


    private void updateProfile() {
        String firstname = mFirstName.getText().toString();
        String lastname = mLastName.getText().toString();
        String phone = mPhone.getText().toString();
        String id = mId.getText().toString();
        HashMap<String, Object> map = new HashMap<>();
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                map.put("firstname", firstname);
                map.put("lastname", lastname);
                map.put("id", id);
                map.put("phone", phone);
                uploadImage(General.PROFILE_IMAGES_URL);

                map.put("bitmap", mBitmapName);
                postRef.updateChildren(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}