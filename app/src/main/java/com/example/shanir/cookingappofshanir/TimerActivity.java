package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.utils.TimerAsyncTask;
import com.example.shanir.cookingappofshanir.utils.TimerUtilities;

import java.sql.Time;

public class TimerActivity extends AppCompatActivity {
    int mTotalTimeInMinutes;
    TextView mTimerTextView;
    ProgressBar mTimerProgressBar;
    Button mStopButton, mStartButton;
    TimerAsyncTask timerAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_activity);
        mTimerTextView = (TextView) findViewById(R.id.timer_textview);

        this.initStopButton();
        this.initStartButton();
        this.initCancelButton();
        this.initProgressBar();

        TimerUtilities timerUtilities = new TimerUtilities(mTotalTimeInMinutes);
        Time timertext = new Time(timerUtilities.getHour(), timerUtilities.getMinutes(), 0);
        mTimerTextView.setText(timertext.toString());
    }

    private void initProgressBar(){
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mTotalTimeInMinutes = intent.getExtras().getInt("totalTimeInMinutes");

        }

        mTimerProgressBar = (ProgressBar) findViewById(R.id.timer_progressbar);
        mTimerProgressBar.setMax(mTotalTimeInMinutes * 60);
        mTimerProgressBar.setProgress(mTotalTimeInMinutes * 60);
    }



    private void initStartButton() {
        this.mStartButton = findViewById(R.id.start_button);

        this.mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStopButton.setEnabled(true);
                mStartButton.setEnabled(false);

                if (timerAsyncTask == null) {
                    timerAsyncTask = new TimerAsyncTask(mTimerTextView, mTimerProgressBar, TimerActivity.this);
                    timerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            mTotalTimeInMinutes);
                } else
                    timerAsyncTask.setIsRunning(true);
            }
        });
    }

    private void initStopButton() {
        this.mStopButton = findViewById(R.id.stop_button);
        this.mStopButton.setEnabled(false);

        this.mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStopButton.setEnabled(false);
                timerAsyncTask.setIsRunning(false);
                mStartButton.setEnabled(true);
            }
        });
    }

    private void initCancelButton() {
        findViewById(R.id.cancel_timer_textview)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
    }

    public void letUserRestartTimer() {
        timerAsyncTask = null;
        mStopButton.setEnabled(false);
        mStartButton.setEnabled(true);
    }
}

