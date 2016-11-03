package com.consultpal.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;

import com.consultpal.android.utils.SharedPrefManager;


/**
 * Created by ines on 5/29/16.
 */
public class CountdownService extends Service {
    public static final String COUNTDOWN_BR = "com.consultpal.android.countdown_br";
    public static final String COUNTDOWN_UPDATE = "com.consultpal.android.countdown_update";
    public static final String COUNTDOWN_FINISH = "com.consultpal.android.countdown_finish";

    Intent bi = new Intent(COUNTDOWN_BR);
    Intent biUpdate = new Intent(COUNTDOWN_UPDATE);
    private long minuteInMillis = 60000;
    private final static int INTERVAL = 1000 * 60; //1 minute

    private static CountDownTimer cdt = null;
    Handler mHandler = new Handler();
    private SharedPrefManager sharedPrefManager;
    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            sendUpdatedSymptoms();
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    public CountdownService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        int maxMin = sharedPrefManager.getTimesInMin();
        cdt = new CountDownTimer(minuteInMillis * maxMin, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                // update UI every second
                bi.putExtra(COUNTDOWN_BR, millisUntilFinished);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                bi.putExtra(COUNTDOWN_FINISH, true);
                sendBroadcast(bi);
                mHandler.removeCallbacks(mHandlerTask);
                stopSelf();
            }
        };
        // start countdown timer
        cdt.start();

        // start 1 minute updates to backend
        mHandlerTask.run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mHandlerTask);
        cdt.cancel();
    }

    private void sendUpdatedSymptoms() {
        biUpdate.putExtra(COUNTDOWN_UPDATE, true);
        System.out.println("SERVICE: SENDUPDATEDSYMPTOMS");
        sendBroadcast(biUpdate);
    }
}
