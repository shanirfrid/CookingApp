package com.example.shanir.cookingappofshanir;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.FileHelper;
import com.example.shanir.cookingappofshanir.classs.Permission;
import com.example.shanir.cookingappofshanir.classs.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etpass, etfirstname, etemail, etlastname, etphone, etid, etconfirmpass;
    TextView tvBack;
    Button btsave;
    String namebitmap;
    ScrollView scrollView;
    ProgressBar pb;
    public static User user;
    private FirebaseAuth mAuth;
    public static final String TAG = "myapp";
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    ImageView mProfileImage;
    Uri uriProfileImage;
    private int GALLERY = 1, CAMERA = 2;
    final String PIC_FILE_NAME_PROFIL = "userpicprofil";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etpass = (EditText) findViewById(R.id.etpasswardregister);
        etconfirmpass = (EditText) findViewById(R.id.etconfirmPassword);
        etfirstname = (EditText) findViewById(R.id.etfirstname);
        etemail = (EditText) findViewById(R.id.etEmail);
        etid = (EditText) findViewById(R.id.etid);
        etphone = (EditText) findViewById(R.id.etPhone);
        etlastname = (EditText) findViewById(R.id.etlastname);
        pb = (ProgressBar) findViewById(R.id.progressbarregister);
        mAuth = FirebaseAuth.getInstance();
        btsave = (Button) findViewById(R.id.btSaveregister);
        mProfileImage = (ImageView) findViewById(R.id.iv_account_profile);
        tvBack = (TextView) findViewById(R.id.tv_back);
        scrollView = (ScrollView) findViewById(R.id.sv_signUp);
        btsave.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        mProfileImage.setOnClickListener(this);

//        chooseImageDialog = new ChooseImageDialog (getApplicationContext(), this, mProfileImage);

        storageReference = FirebaseStorage.getInstance().getReference();

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                etemail.clearFocus();
                etconfirmpass.clearFocus();
                etpass.clearFocus();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mProfileImage){
            Permission permission = new Permission(this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
        }
        if (v == btsave) {

            registerUser();

        } else if (v == tvBack) {
            finish();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
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
                    mProfileImage.setImageBitmap(bitmapGallery);

                    Toast.makeText(RegisterActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {

            Bitmap bitmapCamera = (Bitmap) data.getExtras().get("data");
            mProfileImage.setImageBitmap(bitmapCamera);
            FileHelper.saveBitmapToFile(bitmapCamera, RegisterActivity.this, PIC_FILE_NAME_PROFIL);
            File tmpFile = new File(getFilesDir() + "/" + PIC_FILE_NAME_PROFIL);
            uriProfileImage = Uri.fromFile(tmpFile);
            Log.d("dd", "onActivityResult: " + uriProfileImage);
            Toast.makeText(RegisterActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("imagesprofil/").child(namebitmap);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegisterActivity.this,"Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this," Failed", Toast.LENGTH_SHORT).show();
            }
        })
        ;
    }



    private void registerUser() {
        String password = etpass.getText().toString().trim();
        String email = etemail.getText().toString().trim();
        String confirmspass = etconfirmpass.getText().toString().trim();

        if (!confirmspass.equals(password)) {
            etpass.setError("Passwords do not match");
            etpass.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etemail.setError("Email required");
            etemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError("Please enter a valid email");
            etemail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etpass.setError("Password required");
            etpass.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etpass.setError("Passwords should be a minimum of 6 characters in length ");
            etpass.requestFocus();
            return;
        }

        pb.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pb.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            if (uriProfileImage!= null) {
                                namebitmap = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
                                uploadImage(uriProfileImage);
                            }
                            adduser();
                            Toast.makeText(RegisterActivity.this, "Signed in successfully",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(RegisterActivity.this, "OOPS! you are already signed in",
                                        Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            }
                        }
                    }
                });
    }

    public void adduser() {
        String firstname = etfirstname.getText().toString();
        String lastname = etlastname.getText().toString();
        String pass = etpass.getText().toString();
        String email = etemail.getText().toString();
        String phone = etphone.getText().toString();
        String id = etid.getText().toString();
        if (namebitmap == null)
            user = new User(firstname, lastname, id, pass, phone, email, null, "none");
        else {
            user = new User(firstname, lastname, id, pass, phone, email, null, namebitmap);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user.setIdkey(uid);
        firebaseDatabase.getReference(General.USER_TABLE_NAME).child(uid).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReferfinence) {
                if (databaseError == null) {
                    General.user = user;
                    General.userKey = user.getIdkey();
                    General.userEmail = user.getEmail();
                    Toast.makeText(RegisterActivity.this, "data successfully saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), UserIngredientsActivity.class);
                    startActivity(intent);
                    finish();

                } else
                    Toast.makeText(RegisterActivity.this, "failed to save due to poor network conditions", Toast.LENGTH_SHORT).show();
            }
        });
    }

}









