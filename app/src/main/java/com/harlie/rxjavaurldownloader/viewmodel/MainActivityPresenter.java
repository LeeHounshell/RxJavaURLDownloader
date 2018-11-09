package com.harlie.rxjavaurldownloader.viewmodel;

import android.content.Context;
import android.util.Log;

import com.harlie.rxjavaurldownloader.model.AlbumUrl;


public class MainActivityPresenter {
    static final String TAG = "LEE: " + MainActivityPresenter.class.getSimpleName();

    AlbumUrlAdapter adapter;
    Context context;

    public MainActivityPresenter(AlbumUrlAdapter adapter, Context context) {
        this.adapter = adapter;
        this.context = context;
    }

    public void doClick(AlbumUrl albumUrl) {
        Log.d(TAG, "-click-" + albumUrl);
    }
}
