package com.harlie.urldownloaderlibrary.retrofit;

import android.util.Log;

import com.harlie.urldownloaderlibrary.IJobQueue;

import java.io.IOException;

import okhttp3.Interceptor;


//from: https://stackoverflow.com/questions/41892696/is-it-possible-to-show-progress-bar-when-download-via-retrofit-2-asynchronous
public class DownloadProgressInterceptor implements Interceptor {
    static final String TAG = "LEE: " + DownloadProgressInterceptor.class.getSimpleName();

    private DownloadProgressListener listener;
    private IJobQueue jobQueue;
    private String url;


    public DownloadProgressInterceptor(DownloadProgressListener listener, IJobQueue jobQueue, String url) {
        this.listener = listener;
        this.jobQueue = jobQueue;
        this.url = url;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Log.d(TAG, "intercept");
        okhttp3.Response originalResponse = chain.proceed(chain.request());

        DownloadProgressResponseBody responseBody = new DownloadProgressResponseBody(originalResponse.body(), listener, jobQueue, chain.request().url().toString());
        okhttp3.Response response = originalResponse.newBuilder()
                .body(responseBody)
                .build();
        return response;
    }
}
