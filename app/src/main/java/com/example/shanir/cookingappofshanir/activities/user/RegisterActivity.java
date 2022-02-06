package com.example.shanir.cookingappofshanir.activities.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.db.DbReference;
import com.example.shanir.cookingappofshanir.utils.db.DbConstants;
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
                findViewById(R.id.register_progress_bar));


        mFireBaseAuth = FirebaseAuth.getInstance();
        initSaveButton();
        initProfileImageView();
        initBackTextView();
        initScrollView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initScrollView() {
        mScrollView = findViewById(R.id.register_profile_details_scroll_view);
        mScrollView.setOnTouchListener((view, motionEvent) -> {
            mEmailEditText.clearFocus();
            mConfirmPassEditText.clearFocus();
            mPasswordEditText.clearFocus();
            return false;
        });
    }

    private void initBackTextView() {
        mBackTextView = findViewById(R.id.register_back_text_view);
        mBackTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initSaveButton() {
        mSaveButton = findViewById(R.id.register_sign_up_button);
        mSaveButton.setOnClickListener(v -> registerUser());
    }

    private void initProfileImageView() {
        mImageView = findViewById(R.id.register_profile_image_view);
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
                        return;
                    }

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterActivity.this, "OOPS! Email already in-use!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    mProgressBarManager.requestGone();
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
                Intent intent = new Intent(getApplicationContext(), UserIngredientsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else
                Toast.makeText(RegisterActivity.this,
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}









