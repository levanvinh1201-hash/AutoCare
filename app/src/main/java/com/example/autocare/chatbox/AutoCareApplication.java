package com.example.autocare.chatbox;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

public class AutoCareApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(
                        DebugAppCheckProviderFactory.getInstance()
                );
    }
}
