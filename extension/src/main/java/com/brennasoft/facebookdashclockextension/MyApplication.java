package com.brennasoft.facebookdashclockextension;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(!BuildConfig.DEBUG) {
            Crashlytics.start(this);
        }
    }
}
