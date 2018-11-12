package com.harlie.urldownloaderlibrary.retrofit;

import android.util.Log;

import com.harlie.urldownloaderlibrary.Job;
import com.harlie.urldownloaderlibrary.UrlResult;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;


//from: https://stackoverflow.com/questions/41892696/is-it-possible-to-show-progress-bar-when-download-via-retrofit-2-asynchronous
public class DownloadProgressInterceptor implements Interceptor {
    static final String TAG = "LEE: " + DownloadProgressInterceptor.class.getSimpleName();

    private DownloadProgressListener listener;
    private Map<String, UrlResult> urlResultMap;
    private String url;


    public DownloadProgressInterceptor(DownloadProgressListener listener, Map<String,UrlResult> urlResultMap, String url) {
        this.listener = listener;
        this.urlResultMap = urlResultMap;
        this.url = url;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Log.d(TAG, "intercept");
        okhttp3.Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body(), listener, urlResultMap, chain.request().url().toString()))
                .build();
    }
}
