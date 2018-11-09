package com.harlie.rxjavaurldownloader.model;

//NOTE:  I am using the fake json server at https://jsonplaceholder.typicode.com/
//       to create a "URL Download" list with AlbumUrl photo data.   This POJO class
//       is used to initialize as list of "URL Download" jobs containing AlbumUrl data.

import com.google.gson.annotations.SerializedName;


public class AlbumUrl {
    @SerializedName("id")
    private int id;
    @SerializedName("albumId")
    private int albumId;
    @SerializedName("title")
    private String albumTitle;
    @SerializedName("url")
    private String albumPhotoUrl;
    @SerializedName("thumbnailUrl")
    private String albumThumbnailUrl;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumPhotoUrl() {
        return albumPhotoUrl;
    }

    public void setAlbumPhotoUrl(String albumPhotoUrl) {
        this.albumPhotoUrl = albumPhotoUrl;
    }

    public String getAlbumThumbnailUrl() {
        return albumThumbnailUrl;
    }

    public void setAlbumThumbnailUrl(String albumThumbnailUrl) {
        this.albumThumbnailUrl = albumThumbnailUrl;
    }

    @Override
    public String toString() {
        return "AlbumUrl{" +
                "id=" + id +
                ", albumId=" + albumId +
                ", albumTitle='" + albumTitle + '\'' +
                ", albumPhotoUrl='" + albumPhotoUrl + '\'' +
                ", albumThumbnailUrl='" + albumThumbnailUrl + '\'' +
                '}';
    }
}
