package com.harlie.rxjavaurldownloader.model;

//NOTE:  I am using the fake json server at https://jsonplaceholder.typicode.com/
//       to create a "URL Download" list with AlbumUrl photo data.   This POJO class
//       is used to initialize as list of "URL Download" jobs containing AlbumUrl data.

import android.content.res.Resources;

import com.google.gson.annotations.SerializedName;
import com.harlie.rxjavaurldownloader.R;
import com.harlie.rxjavaurldownloader.RxJavaUrlDownloaderApplication;

import static com.harlie.rxjavaurldownloader.model.AlbumUrl.AlbumUrlStatus.UNSELECTED;


public class AlbumUrl {

    public enum AlbumUrlStatus {
        UNSELECTED,
        SELECTED,
        QUEUED,
        DOWNLOADING,
        COMPLETE
    }

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

    private AlbumUrlStatus albumUrlStatus;
    private int jobId;


    public AlbumUrl() {
        this.albumUrlStatus = UNSELECTED;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        Resources resources = RxJavaUrlDownloaderApplication.getAppContext().getResources();
        String id = "";
        if (jobId == 0) {
            id = resources.getString(R.string.job_id) + ": " + resources.getString(R.string.empty_job_id);
        }
        else {
            id = resources.getString(R.string.job_id) + ": " + jobId;
        }
        return id;
    }

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

    public AlbumUrlStatus getAlbumUrlStatus() {
        //TODO: retrieve object from internal cache or webserver (along with Jobs list)
        return albumUrlStatus;
    }

    public void setAlbumUrlStatus(AlbumUrlStatus albumUrlStatus) {
        //TODO: save object to internal cache or webserver (along with Jobs list)
        this.albumUrlStatus = albumUrlStatus;
    }

    @Override
    public String toString() {
        return "AlbumUrl{" +
                "id=" + id +
                ", albumId=" + albumId +
                ", albumTitle='" + albumTitle + '\'' +
                ", albumPhotoUrl='" + albumPhotoUrl + '\'' +
                ", albumThumbnailUrl='" + albumThumbnailUrl + '\'' +
                ", albumUrlStatus=" + albumUrlStatus +
                '}';
    }
}
