package com.harlie.urldownloaderlibrary.retrofit;


public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
