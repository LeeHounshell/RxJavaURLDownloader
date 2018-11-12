package com.harlie.urldownloaderlibrary.retrofit;

import android.util.Log;

import com.harlie.urldownloaderlibrary.UrlResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


public class JobRunnerRetrofitClient {
    static final String TAG = "LEE: " + JobRunnerRetrofitClient.class.getSimpleName();

    private static Retrofit retrofit;
    private static URL workingUrl;
    private static URL oldUrl;


    public static Retrofit getRetrofit(String url, DownloadProgressListener listener, long timeOut, Map<String, UrlResult> urlResultMap) {
        Log.d(TAG, "getRetrofit: url=" + url);
        if (retrofit == null) {
            try {
                oldUrl = workingUrl;
                workingUrl = new URL(url);
            } catch (MalformedURLException e) {
                Log.e(TAG, "Invalid URL: " + url + ", e=" + e);
                return null;
            }
            createRetrofit(url, listener, timeOut, urlResultMap);
        }
        else {
            URL newWorkingUrl;
            try {
                oldUrl = workingUrl;
                newWorkingUrl = new URL(url);
            } catch (MalformedURLException e) {
                Log.e(TAG, "Invalid URL: " + url + ", e=" + e);
                return null;
            }
            if (oldUrl.getHost().equals(newWorkingUrl.getHost())
                && oldUrl.getProtocol().equals(newWorkingUrl.getProtocol())
                && oldUrl.getPort() == newWorkingUrl.getPort())
            {
                Log.d(TAG, "host is unchanged, reuse the old retrofit client");
            }
            else {
                Log.d(TAG, "host is changed, create a new retrofit client");
                workingUrl = newWorkingUrl;
                createRetrofit(url, listener, timeOut, urlResultMap);
            }
        }
        return retrofit;
    }

    private static void createRetrofit(String url, DownloadProgressListener listener, long timeOut, Map<String, UrlResult> urlResultMap) {
        Log.d(TAG, "createRetrofit: url=" + url);
        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener, urlResultMap, url);

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    // force using getInstance
    private JobRunnerRetrofitClient() {
    }
}
