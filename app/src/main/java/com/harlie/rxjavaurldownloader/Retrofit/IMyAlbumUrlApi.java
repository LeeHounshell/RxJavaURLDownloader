package com.harlie.rxjavaurldownloader.Retrofit;

import com.harlie.rxjavaurldownloader.Model.AlbumUrl;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;


public interface IMyAlbumUrlApi {
    @GET("photos")
    Observable<List<AlbumUrl>> getAlbumPhotos();
}
