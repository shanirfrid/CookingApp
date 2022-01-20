package com.example.shanir.cookingappofshanir.utils;

import androidx.annotation.NonNull;

/**
 * Created by Shanir on 21/04/2018.
 */

public class TimerUtilities {
    private final int mTotalMinutes;

    public TimerUtilities(int totalMinutes) {
        this.mTotalMinutes = totalMinutes;
    }

    public int getMinutes()
    {
        return  mTotalMinutes - (getHour() *  60);
    }

    public int getHour()
    {
        return mTotalMinutes / 60;
    }
}
