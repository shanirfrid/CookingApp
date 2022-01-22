package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shanir.cookingappofshanir.Admin.AdminRecipesActivity;
import com.example.shanir.cookingappofshanir.utils.DbConstants;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
    Button mSignInButton;
    TextView mSignUpTextView;
    EditText mPasswordEditText, mEmailEditText;
    ProgressBar mProgressBar;
    FirebaseAuth mFireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailEditText = findViewById(R.id.etemaillogin);
        mPasswordEditText = findViewById(R.id.etPasswordlogin);
        mProgressBar = findViewById(R.id.progressbarlogin);

        mFireBaseAuth = FirebaseAuth.getInstance();
        initSignInButton();
        initSignUpTextView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFireBaseAuth.getCurrentUser() == null)
            return;
        passUserToSuitablePage();
    }

    private void passUserToSuitablePage(){
        Intent intent;
        if (mFireBaseAuth.getCurrentUser().getEmail()
                .equals(DbConstants.ADMIN_EMAIL))
            intent = new Intent(getApplicationContext(), AdminRecipesActivity.class);
        else
            intent = new Intent(getApplicationContext(), UserIngredientsActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void initSignUpTextView() {
        mSignUpTextView = findViewById(R.id.tvSignUp);
        mSignUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initSignInButton() {
        mSignInButton = findViewById(R.id.btSignIn);
        mSignInButton.setOnClickListener(v -> userLogin());
    }

    private void userLogin() {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

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
            mPasswordEditText.setError("The minimal length of the password is 6 characters");
            mPasswordEditText.requestFocus();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mFireBaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            mProgressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                passUserToSuitablePage();
            }
            else{
                Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}

