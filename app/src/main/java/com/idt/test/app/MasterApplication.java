package com.idt.test.app;

import android.app.Application;

import com.idt.test.app.function.MyPreference;

/**
 * Created by Omid Heshmatinia on 9/2/2016.
 */
public class MasterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyPreference.getInstance(this);
    }
}
