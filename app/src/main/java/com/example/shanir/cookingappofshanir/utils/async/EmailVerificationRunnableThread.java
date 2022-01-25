package com.example.shanir.cookingappofshanir.utils.async;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.shanir.cookingappofshanir.activities.user.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class EmailVerificationRunnableThread implements Runnable {
    private final UserProfileActivity mUserProfileActivity;
    private final FirebaseAuth mFirebaseAuth;
    private final Handler mHandler;

    public EmailVerificationRunnableThread(UserProfileActivity userProfileActivity) {
        mUserProfileActivity = userProfileActivity;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mHandler = new Handler();
    }

    public void start() {
        mHandler.post(this);
    }

    @Override
    public void run() {
        mFirebaseAuth.getCurrentUser().reload().addOnCompleteListener(task -> {
            if (!mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                mHandler.postDelayed(this, 1000);
                return;
            }
            mUserProfileActivity.changeEmailToVerified();
        });
    }
}
