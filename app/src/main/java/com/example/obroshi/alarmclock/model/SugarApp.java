package com.example.obroshi.alarmclock.model;

import android.app.Application;
import android.util.Log;

import com.orm.SugarContext;

public class SugarApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
        Log.d("TAG", "SugarContext**********");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

}