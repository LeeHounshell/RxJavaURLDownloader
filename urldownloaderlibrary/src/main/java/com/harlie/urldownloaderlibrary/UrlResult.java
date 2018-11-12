package com.harlie.urldownloaderlibrary;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okio.ByteString;


public class UrlResult {
    static final String TAG = "LEE: " + UrlResult.class.getSimpleName();

    enum Result {
        URL_FAILED,
        URL_CANCELLED,
        URL_COMPLETED
    }

    private String url;
    private String theFilePath;
    private ByteString sha1;
    private int retryCount;
    private Result result;


    public UrlResult(String url) {
        this.url = url;
        this.theFilePath = null;
        this.retryCount = 0;
        this.sha1 = null;
        setResultFail(); // job not even started yet
    }

    public String getUrl() {
        return url;
    }

    public String getTheFilePath() {
        return theFilePath;
    }

    public void setTheFilePath(String theFilePath) {
        this.theFilePath = theFilePath;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public Result getResult() {
        return result;
    }

    public void setResultFail() {
        Log.d(TAG, "setResultFail");
        this.result = Result.URL_FAILED;
    }

    public void setResultCancelled() {
        Log.d(TAG, "setResultCancelled");
        this.result = Result.URL_CANCELLED;
    }

    public void setResultCompleted() {
        Log.d(TAG, "setResultCompleted");
        this.result = Result.URL_COMPLETED;
    }

    public ByteString getSha1() {
        return sha1;
    }

    public void setSha1(ByteString sha1) {
        this.sha1 = sha1;
    }

    @Override
    public String toString() {
        return "UrlResult{" +
                "url='" + url + '\'' +
                ", theFilePath='" + theFilePath + '\'' +
                ", sha1=" + sha1 +
                ", retryCount=" + retryCount +
                ", result='" + result + '\'' +
                '}';
    }
}
