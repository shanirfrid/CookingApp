package com.example.shanir.cookingappofshanir.utils;

import android.view.View;
import android.widget.ProgressBar;

public class ProgressBarManager {
    private final ProgressBar mProgressBar;
    private int mVisibleRequests;

    public ProgressBarManager(ProgressBar progressBar) {
        mProgressBar = progressBar;
        mVisibleRequests = 0;
    }

    public void requestVisible() {
        mVisibleRequests++;
        updateVisibility();
    }

    public void requestGone() {
        mVisibleRequests = Math.max(mVisibleRequests - 1, 0);
        updateVisibility();
    }

    public void updateVisibility() {
        mProgressBar.setVisibility(mVisibleRequests > 0 ? View.VISIBLE :
                View.GONE);
    }
}
