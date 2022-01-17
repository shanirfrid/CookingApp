package com.example.shanir.cookingappofshanir.classs;

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
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.MainActivity;
import com.example.shanir.cookingappofshanir.Profile;
import com.example.shanir.cookingappofshanir.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Nonnull;


public class UpdateProfile extends AppCompatActivity implements View.OnClickListener {

    EditText mFirstName, mLastName, mId, mPhone;
    Button button;
    TextView tvEditProfileImage;
    String tableusers;
    FirebaseAuth mAuth;
    ImageView mProfileImage, mExit;
    private int GALLERY = 1, CAMERA = 2;
    String namebitmap = "none";
    final String PIC_FILE_NAME_PROFIL = "userpicprofil";
    Uri uriProfileImage;
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
        mProfileImage = (ImageView) findViewById(R.id.iv_update_profile_image);
        mPb = (ProgressBar) findViewById(R.id.pb_update_profile);
        mExit = (ImageView) findViewById(R.id.close_image_view);
        tvEditProfileImage = (TextView) findViewById(R.id.tv_edit_profile_image);

        Intent i = getIntent();
        if (i.getExtras() != null) {
            namebitmap = i.getExtras().getString("bitmap");
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
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        } else if (v == tvEditProfileImage) {
            Permission permission = new Permission(this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
        } else if (v == mExit) {
            Intent intent = new Intent(this, Profile.class);
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
                mProfileImage.setImageBitmap(bitmap);
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
        ImageView profilePicture = mProfileImage;
        HashMap<String, Object> map = new HashMap<>();
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                map.put("firstname", firstname);
                map.put("lastname", lastname);
                map.put("id", id);
                map.put("phone", phone);

                if (uriProfileImage != null) {
                    namebitmap = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
                    uploadImage(uriProfileImage);
                }
                map.put("bitmap", namebitmap);
                postRef.updateChildren(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Add Photo!");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Take Photo"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                uriProfileImage = data.getData();
                Log.d("dd", "onActivityResult: " + uriProfileImage);
                try {
                    Bitmap bitmapGallery = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriProfileImage);
                    mProfileImage.setImageBitmap(bitmapGallery);
                    imageHasChanged = true;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UpdateProfile.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {

            Bitmap bitmapCamera = (Bitmap) data.getExtras().get("data");
            mProfileImage.setImageBitmap(bitmapCamera);
            imageHasChanged = true;
            FileHelper.saveBitmapToFile(bitmapCamera, UpdateProfile.this, PIC_FILE_NAME_PROFIL);
            File tmpFile = new File(getFilesDir() + "/" + PIC_FILE_NAME_PROFIL);
            uriProfileImage = Uri.fromFile(tmpFile);
            Log.d("dd", "onActivityResult: " + uriProfileImage);
            Toast.makeText(UpdateProfile.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("imagesprofil/").child(namebitmap);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UpdateProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfile.this, " Failed", Toast.LENGTH_SHORT).show();

            }
        })
        ;
    }


}