package com.harlie.rxjavaurldownloader;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

public class RxJavaUrlDownloaderApplication extends MultiDexApplication {
    private final static String TAG = "LEE: <" + RxJavaUrlDownloaderApplication.class.getSimpleName() + ">";

    private static RxJavaUrlDownloaderApplication sInstance;


    public void onCreate() {
        Log.v(TAG, "===> onCreate <===");
        RxJavaUrlDownloaderApplication.sInstance = this;
        super.onCreate();
    }

    public static RxJavaUrlDownloaderApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }

}

