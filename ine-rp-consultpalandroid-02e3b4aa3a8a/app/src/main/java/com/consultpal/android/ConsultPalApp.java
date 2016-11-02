package com.consultpal.android;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ines on 6/9/16.
 */
public class ConsultPalApp extends Application {

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            /* This part of code is used for debugging
            analytics.setDryRun(true);
            mTracker = analytics.newTracker("UA-86490574-2");
            mTracker.setSampleRate(50.0d);*/
        }
        return mTracker;
    }
}
