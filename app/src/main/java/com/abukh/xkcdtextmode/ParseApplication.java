package com.abukh.xkcdtextmode;

import android.app.Application;

import com.parse.Parse;
import android.app.Application;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("rWT6DY8xYhQYfvU7zMImEnGEQr4ZyMSZORrQyWjX")
                .clientKey("ALT6mWWD2nNGun5V7IK6cLNd84kbRTLL7mU46Exq")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}


