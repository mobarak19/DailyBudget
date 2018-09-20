package com.cse.cou.mobarak.dailybudget;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by mobarak on 8/13/2018.
 */

public class DailyBudget extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
