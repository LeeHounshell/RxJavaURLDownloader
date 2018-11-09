package com.harlie.rxjavaurldownloader.retrofit;

import com.harlie.rxjavaurldownloader.model.AlbumUrl;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;


public interface IMyAlbumUrlApi {
    @GET("photos")
    Observable<List<AlbumUrl>> getAlbumPhotos();
}
