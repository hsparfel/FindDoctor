package com.pouillos.finddoctor;

import android.app.Application;

public class MyApp extends Application {
//sert juste à recup context
    private static MyApp instance;
    public MyApp() {
        instance = this;
    }

    public static MyApp getInstance() {
        return instance;
    }
}
