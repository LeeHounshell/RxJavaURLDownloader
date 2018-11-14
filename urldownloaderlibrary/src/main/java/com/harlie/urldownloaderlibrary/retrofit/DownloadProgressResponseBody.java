package com.harlie.urldownloaderlibrary.retrofit;

import android.util.Log;

import com.harlie.urldownloaderlibrary.IJobQueue;
import com.harlie.urldownloaderlibrary.UrlResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


//from: https://stackoverflow.com/questions/41892696/is-it-possible-to-show-progress-bar-when-download-via-retrofit-2-asynchronous
public class DownloadProgressResponseBody extends ResponseBody {
    static final String TAG = "LEE: " + DownloadProgressResponseBody.class.getSimpleName();

    private ResponseBody responseBody;
    private DownloadProgressListener progressListener;
    private BufferedSource bufferedSource;
    private IJobQueue jobQueue;
    private String url;

    public DownloadProgressResponseBody(ResponseBody responseBody,
                                        DownloadProgressListener progressListener,
                                        IJobQueue jobQueue,
                                        String url) {
        Log.d(TAG, "DownloadProgressResponseBody");
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.jobQueue = jobQueue;
        this.url = url;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        Log.d(TAG, "BufferedSource source");
        if (bufferedSource == null) {
            Log.w(TAG, "create the BufferedSource");
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        else {
            Log.w(TAG, "reuse the BufferedSource");
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        Log.d(TAG, "Source source");
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                Log.d(TAG, "---------> read: bytesRead=" + bytesRead);
                if (bytesRead == -1) {
                    if (jobQueue != null) {
                        Log.d(TAG, "read: jobQueue.getUrlResultMap().get(" + url + ");");
                        UrlResult urlResult = jobQueue.getUrlResultMap().get(url);
                        if (urlResult == null) {
                            Log.w(TAG, "read: the urlResult is null! url=" + url);
                            urlResult = new UrlResult(url);
                            jobQueue.getUrlResultMap().put(url, urlResult);
                        }
                        ByteString sha1 = sink.sha1();
                        Log.d(TAG, "read: setSha1 in urlResultMap: sha1=" + sha1);
                        urlResult.setSha1(sha1.toByteArray());
                        urlResult.setResultCompleted();
                    }
                    else {
                        Log.w(TAG, "read: the jobQueue is null! can't set SHA-1");
                    }
                }
                else {
                    totalBytesRead += bytesRead;
                }
                if (null != progressListener) {
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };

    }

}
