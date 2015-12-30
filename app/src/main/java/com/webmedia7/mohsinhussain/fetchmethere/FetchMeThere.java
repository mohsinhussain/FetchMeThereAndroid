package com.webmedia7.mohsinhussain.fetchmethere;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by mohsinhussain on 6/29/15.
 */
public class FetchMeThere extends Application {
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    private static FetchMeThere instance;
    private static Context mContext;

    public static FetchMeThere getInstance() {
        return instance;
    }

    public static Context getContext() {
        //  return instance.getApplicationContext();
        return mContext;
    }


    @Override
    public void onCreate() {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        mContext = getApplicationContext();
        tracker = analytics.newTracker("UA-64606335-1"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        instance = this;
    }
}
