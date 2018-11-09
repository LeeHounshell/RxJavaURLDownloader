package com.harlie.rxjavaurldownloader.retrofit;

import com.harlie.rxjavaurldownloader.model.AlbumUrl;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;


public interface IMyAlbumUrlApi {
    static final String ALBUM_WEBSERVER_URL = "https://jsonplaceholder.typicode.com/";
    static final String ALBUM_DOWNLOAD_URL  = "photos";

    @GET(ALBUM_DOWNLOAD_URL)
    Observable<List<AlbumUrl>> getListOfAlbumPhotoUrl();
}
