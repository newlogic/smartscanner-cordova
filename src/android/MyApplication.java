package com.impactlabs.application;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.impactlabs.mlkitdemo.BuildConfig;

import timber.log.Timber;

public class MyApplication extends Application
{

    @Override
    public void onCreate()
    {
        // DO SOME STUFF
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }


    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {

        private final String KEY_PRIORITY = "priority";
        private final String KEY_TAG = "tag";
        private final String KEY_MESSAGE = "message";

        @Override protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return;
            }

            Crashlytics.setInt(KEY_PRIORITY,priority);
            Crashlytics.setString(KEY_TAG, tag);
            Crashlytics.setString(KEY_MESSAGE, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                   Crashlytics.logException(t);
                } else if (priority == Log.WARN) {
                  //TODO Handle warnings
                }
            }else {
                if (priority == Log.ERROR) {
                    Crashlytics.logException(new Exception(message));
                }
            }
        }
    }
}