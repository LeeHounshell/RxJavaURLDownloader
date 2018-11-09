package com.harlie.rxjavaurldownloader.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class AlbumUrlRetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance(String url) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            return retrofit;
        }
        return retrofit;
    }

    // force using getInstance
    private AlbumUrlRetrofitClient() {
    }
}
