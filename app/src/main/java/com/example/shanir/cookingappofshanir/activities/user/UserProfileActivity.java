package com.example.shanir.cookingappofshanir.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.DbConstants;
import com.example.shanir.cookingappofshanir.utils.ImageUtilities;
import com.example.shanir.cookingappofshanir.utils.NavigationMenu;
import com.example.shanir.cookingappofshanir.utils.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView mProfileImageView, mMenuImageView;
    private Button mEditProfileButton;
    private TextView mVerifyEmailTextView, mUserDetailsTextView,
            mHeaderVerificationEmailTextView, mVerificationEmailTextView,
            mEmailIconTextView;
    private ProgressBar mProgressBar;
    private User mUser;
    private FirebaseAuth mFireBaseAuth;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mUserDetailsTextView = findViewById(R.id.user_profile_details_text_view);
        mHeaderVerificationEmailTextView = findViewById(R.id.user_profile_verification_header_text_view);
        mVerificationEmailTextView = findViewById(R.id.user_profile_verification_body_text_view);
        mEmailIconTextView = findViewById(R.id.user_profile_email_text_view);
        mProgressBar = findViewById(R.id.user_profile_progress_bar);
        mProfileImageView = findViewById(R.id.user_profile_image_view);

        mFireBaseAuth = FirebaseAuth.getInstance();

        initMenu();
        retrieveUserDetails();
        initVerifyEmailTextView();
        initEditProfileButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveUserDetails();
        if (mFireBaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

    private void initEditProfileButton() {
        mEditProfileButton = findViewById(R.id.user_profile_edit_button);
        mEditProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this,
                    EditProfileActivity.class);
            intent.putExtra("bitmap", mUser.getBitmap());
            startActivity(intent);
        });
    }

    private void initMenu() {
        mMenuImageView = findViewById(R.id.user_profile_menu_image_view);
        mDrawerLayout = findViewById(R.id.user_profile_drawer_layout);
        mNavigationView = findViewById(R.id.user_profile_navigation_menu);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationMenu(this, mMenuImageView, mDrawerLayout));
    }

    private void initVerifyEmailTextView() {
        final FirebaseUser user = mFireBaseAuth.getCurrentUser();
        mVerifyEmailTextView = findViewById(R.id.user_profile_verify_button);

        if (user == null)
            return;

        user.reload().addOnSuccessListener(task -> {
            if (mFireBaseAuth.getCurrentUser().isEmailVerified())
                changeEmailToVerified();
            else
                mVerifyEmailTextView.setText("Verify Email");
        });

        mVerifyEmailTextView.setOnClickListener(v ->
                mFireBaseAuth.getCurrentUser().sendEmailVerification()
                        .addOnCompleteListener(task ->
                                Toast.makeText(UserProfileActivity.this,
                                        "Email verification has been sent",
                                        Toast.LENGTH_SHORT).show()));
    }

    private void changeEmailToVerified() {
        mVerifyEmailTextView.setVisibility(View.GONE);
        mHeaderVerificationEmailTextView.setText("Verification Success");
        mVerificationEmailTextView.setText("Thank you for your support, we have successfully verfied your email");
        mEmailIconTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.email_verified_successfully));
    }

    private void retrieveUserDetails() {
        DbReference.getDbRefToUser(mFireBaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);

                if (mUser == null)
                    return;

                mUserDetailsTextView.setText(mUser.toString());

                if (!mUser.getBitmap().equals("none"))
                    ImageUtilities.loadImage
                            (DbConstants.APP_PROFILE_IMAGES_FULL_URL +
                                            mUser.getBitmap(),
                                    mProfileImageView, mProgressBar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}



