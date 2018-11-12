package com.harlie.urldownloaderlibrary.retrofit;

import android.util.Log;

import com.harlie.urldownloaderlibrary.Job;
import com.harlie.urldownloaderlibrary.UrlResult;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


//from: https://stackoverflow.com/questions/41892696/is-it-possible-to-show-progress-bar-when-download-via-retrofit-2-asynchronous
public class DownloadProgressResponseBody extends ResponseBody {
    static final String TAG = "LEE: " + DownloadProgressResponseBody.class.getSimpleName();

    private ResponseBody responseBody;
    private DownloadProgressListener progressListener;
    private BufferedSource bufferedSource;
    private Map<String, UrlResult> urlResultMap;
    private String url;

    public DownloadProgressResponseBody(ResponseBody responseBody,
                                        DownloadProgressListener progressListener,
                                        Map<String, UrlResult> urlResultMap,
                                        String url) {
        Log.d(TAG, "DownloadProgressResponseBody");
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.urlResultMap = urlResultMap;
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
            bufferedSource = Okio.buffer(source(responseBody.source()));
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
                Log.d(TAG, "read: bytesRead=" + bytesRead);
                if (bytesRead == -1) {
                    if (urlResultMap != null) {
                        UrlResult urlResult = urlResultMap.get(url);
                        if (urlResult != null) {
                            Log.d(TAG, "read: setSha1 in urlResultMap");
                            urlResult.setSha1(sink.sha1());
                            urlResult.setResultCompleted();
                        }
                        else {
                            Log.w(TAG, "read: the urlResult is null! can't set SHA-1, url=" + url);
                        }
                    }
                    else {
                        Log.w(TAG, "read: the urlResultMap is null! can't set SHA-1");
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
