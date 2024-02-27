package com.s.rubiccubestopwatch;

import android.os.Handler;
import android.widget.TextView;

public class Stopwatch {

    private TextView textView;
    private long startTime;
    private boolean isRunning;
    private Handler handler;
    private Runnable updateTimeTask;
    String time;

    public Stopwatch(TextView textView) {
        this.textView = textView;
        this.isRunning = false;
        this.handler = new Handler();
        initUpdateTimeTask();
    }

    private void initUpdateTimeTask() {
        updateTimeTask = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    updateTextView(elapsedTime);
                    handler.postDelayed(this, 16);
                }
            }
        };
    }

    public void start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            handler.post(updateTimeTask);
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(updateTimeTask);
        }
    }

    public String returnTime() {
        return time;
    }

    private String reTime(){
        return time;
    }

    private void updateTextView(long elapsedTime) {
        long minutes = (elapsedTime / 60000);
        long seconds = (elapsedTime / 1000) % 60;
        long centiseconds = (elapsedTime / 10) % 100;

        if (minutes > 0) {
            time = String.format("%02d.%02d.%02d", minutes, seconds, centiseconds);
        } else {
            time = String.format("%02d.%02d", seconds, centiseconds);
        }

        textView.setText(time);
    }

}
