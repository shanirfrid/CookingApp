package com.example.shanir.cookingappofshanir.utils.async;

import android.content.Context;
import android.os.Vibrator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.VibrationEffect;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import com.example.shanir.cookingappofshanir.activities.user.TimerActivity;
import com.example.shanir.cookingappofshanir.utils.TimerUtilities;

import java.sql.Time;

public class TimerAsyncTask extends AsyncTask<Integer, Integer, String> {
    final int ONE_SECOND_IN_MILLIS = 1000;
    private TextView mTimerTextView;
    private ProgressBar mProgressBar;
    private Context mContext;
    private Time mTimerFormat;
    private TimerUtilities mTimerUtilities;
    private boolean mIsRunning;
    private TimerActivity mTimerRunActivity;

    public TimerAsyncTask(TextView timerTextView, ProgressBar progressBar,
                          Context context) {
        this.mTimerTextView = timerTextView;
        this.mProgressBar = progressBar;
        this.mIsRunning = true;
        this.mContext = context;
        this.mTimerRunActivity = (TimerActivity) context;
    }

    public void setIsRunning(boolean isRunning) {
        mIsRunning = isRunning;
    }

    @Override
    protected String doInBackground(Integer... integers) {
        int totalTimeInMinutes = integers[0];
        mTimerUtilities = new TimerUtilities(totalTimeInMinutes);
        mTimerFormat = new Time(mTimerUtilities.getHour(),
                mTimerUtilities.getMinutes(), 0);
        int totalTimeInSeconds = totalTimeInMinutes * 60;

        while (totalTimeInSeconds > 0) {
            if (mIsRunning) {
                try {
                    Thread.sleep(ONE_SECOND_IN_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                totalTimeInSeconds--;
                mTimerFormat.setSeconds(mTimerFormat.getSeconds() - 1);
                publishProgress(totalTimeInSeconds);
            }
        }
        return "time is up!";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        mTimerTextView.setText(mTimerFormat.toString());
        mProgressBar.setProgress(values[0]);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mTimerRunActivity.letUserRestartTimer();
        mTimerTextView.setText(s);
        Vibrator vibrator =
                (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(
                VibrationEffect.createOneShot(ONE_SECOND_IN_MILLIS / 2,
                        VibrationEffect.DEFAULT_AMPLITUDE));

    }
}


