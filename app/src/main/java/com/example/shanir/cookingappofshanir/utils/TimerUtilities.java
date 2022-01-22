package com.example.shanir.cookingappofshanir.utils;

public class TimerUtilities {
    private final int mTotalMinutes;

    public TimerUtilities(int totalMinutes) {
        mTotalMinutes = totalMinutes;
    }

    public int getMinutes()
    {
        return  mTotalMinutes - (this.getHour() *  60);
    }

    public int getHour()
    {
        return mTotalMinutes / 60;
    }
}
