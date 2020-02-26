package com.impactlabs.application;

import android.app.Application;

import com.impactlabs.mlkitdemo.BuildConfig;

import timber.log.Timber;

public class MyApplication extends Application
{
    public static final String TAG = "MyApplication";

    @Override
    public void onCreate()
    {
        // DO SOME STUFF
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
//            Timber.plant(new CrashReportingTree());
        }
    }
}