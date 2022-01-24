package com.example.shanir.cookingappofshanir;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.DbConstants;
import com.example.shanir.cookingappofshanir.utils.Permission;
import com.example.shanir.cookingappofshanir.utils.ProgressBarManager;
import com.example.shanir.cookingappofshanir.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends ImagePromptActivity {
    private EditText mPasswordEditText, mFirstNameEditText, mEmailEditText,
            mLastNameEmail, mPhoneEditText, mIdEditText, mConfirmPassEditText;
    private TextView mBackTextView;
    private Button mSaveButton;
    private ScrollView mScrollView;
    private FirebaseAuth mFireBaseAuth;
    private ProgressBarManager mProgressBarManager;


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
        mProgressBarManager = new ProgressBarManager(
                findViewById(R.id.progressbarregister));


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
            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initSaveButton() {
        mSaveButton = findViewById(R.id.btSaveregister);
        mSaveButton.setOnClickListener(v -> registerUser());
    }

    private void initProfileImageView() {
        mImageView = findViewById(R.id.iv_account_profile);
        mImageView.setOnClickListener(v -> {
            Permission permission = new Permission(RegisterActivity.this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
        });
    }

    private void registerUser() {
        String password = mPasswordEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();
        String confirmPassword = mConfirmPassEditText.getText().toString().trim();

        if (!confirmPassword.equals(password)) {
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
        if (password.length() < DbConstants.MINIMAL_PASSWORD_SIZE) {
            mPasswordEditText.setError("Passwords should be a minimum of 6 characters in length ");
            mPasswordEditText.requestFocus();
            return;
        }

        mProgressBarManager.requestVisible();

        mFireBaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        uploadImage(DbConstants.PROFILE_IMAGES_URL);
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
                mProgressBarManager.requestGone();
                Toast.makeText(RegisterActivity.this, "data successfully saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), UserIngredientsActivity.class);
                startActivity(intent);
                finish();
            } else
                Toast.makeText(RegisterActivity.this,
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}









