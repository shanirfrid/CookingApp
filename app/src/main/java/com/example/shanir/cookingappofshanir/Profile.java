package com.example.shanir.cookingappofshanir;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.FileHelper;
import com.example.shanir.cookingappofshanir.classs.Navigation;
import com.example.shanir.cookingappofshanir.classs.Permission;
import com.example.shanir.cookingappofshanir.classs.User;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    ImageView ivAccountProfile, mRightArrowImageView;
    TextView textView, textViewdetails, tvHeaderVerficationEmail, tvVerficationEmail, tvIconEmail;
    Button btsave;
    String tableusers;
    final String PIC_FILE_NAME_PROFIL = "userpicprofil";
    private DatabaseReference postRef;
    Uri uriProfileImage;
    ProgressBar pb;
    User userprofil;
    String namebitmap;
    FirebaseAuth mAuth;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private int GALLERY = 1, CAMERA = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        textViewdetails = (TextView) findViewById(R.id.tvdetailsprofil);
        ivAccountProfile = (ImageView) findViewById(R.id.profile_iv_account_profile);
        textView = (TextView) findViewById(R.id.tvvreified);
        tvHeaderVerficationEmail = (TextView) findViewById(R.id.tv_header_verify_email);
        tvVerficationEmail = (TextView) findViewById(R.id.tv_verigy_email);
        tvIconEmail = (TextView) findViewById(R.id.tv_icon_email);
        btsave = (Button) findViewById(R.id.btsaveprofile);
        pb = (ProgressBar) findViewById(R.id.pbprofile);
        mRightArrowImageView = (ImageView) findViewById(R.id.right_arrow_image_view);
        btsave.setOnClickListener(this);
        ivAccountProfile.setOnClickListener(this);

        retrieveData();
        loadUserInformation();
        textViewdetails.setVisibility(View.VISIBLE);

        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new Navigation(this));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainLayoutProfile);
        mRightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveData();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified()) {
            changeEmailToVerified();
        }
        else {
            textView.setText("Verify Email");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Profile.this, "Email verification has been sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }

    public void changeEmailToVerified(){
        textView.setVisibility(View.GONE);
        tvHeaderVerficationEmail.setText("Verification Success");
        tvVerficationEmail.setText("Thank you for your support, we have successfully verfied your email");
        tvIconEmail.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.email_verified_successfully));
    }



    public void retrieveData() {
        setRefToTables();
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    userprofil = postSnapshot.getValue(User.class);
                    userprofil.setIdkey(mAuth.getCurrentUser().getUid());

                    if (userprofil.getIdkey().equals(mAuth.getCurrentUser().getUid())) {
                        textViewdetails.setText(userprofil.toString());

                        if (!userprofil.getBitmap().equals("none"))
                            loadImage(userprofil.getBitmap());
                        else {
                            Toast.makeText(getApplicationContext(), "You dont have a profile image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setRefToTables() {
        String uid = mAuth.getCurrentUser().getUid();
        tableusers = General.USER_TABLE_NAME + "/" + uid;
        postRef = FirebaseDatabase.getInstance().getReference(tableusers);
    }

    private void loadImage(String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://cookingappofshanir.appspot.com/imagesprofil/").child(name);
        final long ONE_MEGABYTE = 1024 * 1024 * 5;

        //download file as a byte array
        pb.setVisibility(View.VISIBLE);
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(byte[] bytes) {
                pb.setVisibility(View.GONE);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivAccountProfile.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == ivAccountProfile){
            Permission permission = new Permission(this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
        }
        else if (v == btsave) {
            final FirebaseUser user = mAuth.getCurrentUser();
            if (uriProfileImage!= null) {
                namebitmap = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
                uploadImage(uriProfileImage);
                user.updateProfile(uriProfileImage);
            }
            if (user.isEmailVerified()) {
                changeEmailToVerified();
            }
            user.reload();
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Add Photo!");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Take Photo" };
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
        if (requestCode == GALLERY ) {
            if (data != null) {
                uriProfileImage = data.getData();
                Log.d("dd", "onActivityResult: " + uriProfileImage);
                try {
                    Bitmap bitmapGallery = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriProfileImage);
                    ivAccountProfile.setImageBitmap(bitmapGallery);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Profile.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {

            Bitmap bitmapCamera = (Bitmap) data.getExtras().get("data");
            ivAccountProfile.setImageBitmap(bitmapCamera);
            FileHelper.saveBitmapToFile(bitmapCamera, Profile.this, PIC_FILE_NAME_PROFIL);
            File tmpFile = new File(getFilesDir() + "/" + PIC_FILE_NAME_PROFIL);
            uriProfileImage = Uri.fromFile(tmpFile);
            Log.d("dd", "onActivityResult: " + uriProfileImage);
            Toast.makeText(Profile.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("imagesprofil/").child(namebitmap);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Profile.this,"Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this," Failed", Toast.LENGTH_SHORT).show();

            }
        })
        ;
    }


}



