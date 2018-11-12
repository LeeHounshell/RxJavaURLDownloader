package com.harlie.urldownloaderlibrary;

import android.util.Log;

import com.harlie.urldownloaderlibrary.retrofit.DownloadProgressListener;
import com.harlie.urldownloaderlibrary.retrofit.IRetrofitDownloader;
import com.harlie.urldownloaderlibrary.retrofit.JobRunnerRetrofitClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;


class JobQueueImpl implements IJobQueue {
    static final String TAG = "LEE: " + JobQueueImpl.class.getSimpleName();

    private Job job;
    private Map<String, UrlResult> urlResultMap;
    private Map<String, UrlResult> urlCompleteMap;
    private List<Disposable> disposables;
    private final Object lock = new Object();


    public JobQueueImpl(final Job job) {
        Log.d(TAG, "JobQueueImpl");
        job.setJobState(Job.JobState.JOB_CREATED);
        this.job = job;
        this.urlResultMap = new HashMap<String, UrlResult>();
        this.urlCompleteMap = new HashMap<String, UrlResult>();
        this.disposables = new ArrayList<Disposable>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String url : job.getUrlList()) {
                    urlResultMap.put(url, new UrlResult(url));
                }
            }
        }).start();
    }

    @Override
    public void start(Object jobLock) {
        Log.d(TAG, "start: job=" + job);
        startTheJob();
        synchronized (jobLock) {
            Log.d(TAG, "start: notify job started");
            jobLock.notify();
        }
    }

    private void startTheJob() {
        Log.d(TAG, "startTheJob");
        if (job != null && ! job.isCancelled() && ! job.isComplete()) {
            if (job.isPaused()) {
                Log.d(TAG, "startTheJob: job is paused");
                waitForJobUnpause();
            }
            if (job.isCancelled()) {
                Log.d(TAG, "startTheJob: job is cancelled");
                return;
            }
            job.setJobState(Job.JobState.JOB_RUNNING);
            for (String url : job.getUrlList()) {
                if (job.isPaused()) {
                    Log.d(TAG, "startTheJob: job is paused");
                    waitForJobUnpause();
                }
                if (job.isCancelled()) {
                    Log.d(TAG, "startTheJob: job is cancelled");
                    break;
                }
                job.setJobState(Job.JobState.JOB_RUNNING);
                UrlResult urlResult = urlResultMap.get(url);
                if (urlResult != null) {
                    try {
                        String theBaseUrl;
                        URL theUrl = new URL(url);
                        if (theUrl.getPort() == 80 || theUrl.getPort() < 0) {
                            theBaseUrl = theUrl.getProtocol() + "://" + theUrl.getHost() + "/";
                        }
                        else {
                            theBaseUrl = theUrl.getProtocol() + "://" + theUrl.getHost() + ":" + theUrl.getPort() + "/";
                        }
                        DownloadProgressListener listener = new DownloadProgressListener() {
                            @Override
                            public void update(long bytesRead, long contentLength, boolean done) {
                                Log.d(TAG, "DownloadProgressListener update: bytesRead=" + bytesRead + ", contentLength=" + contentLength + ", done=" + done);
                                //TODO: report download progress
                            }
                        };
                        Retrofit downloader = JobRunnerRetrofitClient.getRetrofit(theBaseUrl, listener, job.getTimeOut(), urlResultMap);
                        if (downloader != null) {
                            startDownload(downloader, url, getPathFromUrl(theBaseUrl, url));
                        }
                        else {
                            Log.e(TAG, "unable to create JobRunnerRetrofitClient!");
                        }
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "unable to convert URL url=" + url + ", e=" + e);
                    }
                }
            }
        }
        else {
            Log.e(TAG, "startTheJob: job not runnable!");
        }
    }

    //TODO: this method may not be robust - needs testing
    public String getPathFromUrl(String theBaseUrl, String url){
        return url.toLowerCase().replace(theBaseUrl.toLowerCase(), "");
    }

    private void startDownload(Retrofit retrofit, final String url, final String path) {
        Log.d(TAG, "startDownload: path=" + path);
        IRetrofitDownloader downloader = retrofit.create(IRetrofitDownloader.class);
        Disposable disposable = downloader
                .urlDownloadRx(path)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                        Log.d(TAG, "accept: response=" + response + ", success=" + response.isSuccessful());
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            saveResponseAsFile(responseBody, url);
                            completeDownloadForUrl(url);
                        }
                        else {
                            Log.e(TAG, "DOWNLOAD FAILED!");
                            job.incrementDownloadFailCount();
                            if (job.getNumberFailures() >= job.getNumberRetrys()) {
                                Log.w(TAG, "cancel job after max retrys reached");
                                job.cancel();
                            }
                            else {
                                Log.w(TAG, "retry job after backoff");
                                job.retryAfterBackoff();
                            }
                        }
                    }
                });
        disposables.add(disposable);
    }

    private void completeDownloadForUrl(String url) {
        Log.d(TAG, "---> completeDownloadForUrl <--- url=" + url);
        UrlResult urlResult = urlResultMap.get(url);
        if (urlResult != null) {
            urlResultMap.remove(url);
            urlCompleteMap.put(url, urlResult);
            if (urlResultMap.size() == 0) {
                Log.d(TAG, "===> JOB COMPLETE!");
                job.setJobState(Job.JobState.JOB_COMPLETE);
                complete();
            }
        }
        else {
            Log.e(TAG, "did not find url in urlResultMap! url=" + url);
        }
    }

    private boolean saveResponseAsFile(ResponseBody body, String url) {
        Log.d(TAG, "saveResponseAsFile: url=" + url);
        try {
            final File theFile = File.createTempFile("download_", Long.toString(System.nanoTime()));
            UrlResult urlResult = getUrlResultMap().get(url);
            urlResult.setTheFilePath(theFile.getAbsolutePath());
            getUrlResultMap().put(url, urlResult);
            Log.d(TAG, "urlResult=" + urlResult);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(theFile);

                while (true) {
                    //TODO: add handling of JOB_PAUSED and JOB_CANCELLED
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                Log.d(TAG, "===> file saved as " + theFile.getAbsolutePath());
                return true;
            } catch (FileNotFoundException e) {
                Log.w(TAG, "save failed, got FileNotFoundException e=" + e);
            } catch (IOException e) {
                Log.w(TAG, "save failed, got IOException e=" + e);
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "file save failed, got IOException e=" + e);
        }
        return false;
    }

    private void waitForJobUnpause() {
        Log.d(TAG, "waitForJobUnpause");
        while (job.getJobState() == Job.JobState.JOB_PAUSED) {
            try {
                Log.d(TAG, "waitForJobUnpause: wait");
                synchronized (lock) {
                    lock.wait();
                }
                Log.d(TAG, "waitForJobUnpause: resume execution");
            } catch (InterruptedException e) {
                Log.e(TAG, "INTERRUPTED: waitForJobUnpause, e=" + e);
            }
        }
        Log.d(TAG, "waitForJobUnpause: done");
    }

    @Override
    public void pause() {
        job.setJobState(Job.JobState.JOB_PAUSED);
        Log.d(TAG, "pause: job=" + job);
    }

    @Override
    public void unpause() {
        Log.d(TAG, "unpause");
        job.setJobState(Job.JobState.JOB_RUNNING);
        synchronized (lock) {
            lock.notify();
            Log.d(TAG, "unpause: did notify");
        }
        Log.d(TAG, "unpause: job=" + job);
    }

    @Override
    public void complete() {
        job.setJobState(Job.JobState.JOB_COMPLETE);
        Log.d(TAG, "complete: job=" + job);
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
        disposables.clear();
        job.complete();
    }

    @Override
    public void cancel() {
        job.setJobState(Job.JobState.JOB_CANCELLED);
        for (String url : urlResultMap.keySet()) {
            UrlResult urlResult = urlResultMap.get(url);
            urlResult.setResultCancelled();
            urlCompleteMap.put(url, urlResult);
        }
        urlResultMap.clear();
        Log.d(TAG, "cancel: job=" + job);
    }

    @Override
    public Job.JobState getJobState() {
        return null;
    }

    public Job getJob() {
        return job;
    }

    @Override
    public Map<String, UrlResult> getUrlResultMap() {
        return urlResultMap;
    }

    @Override
    public Map<String, UrlResult> getUrlCompleteMap() {
        return urlCompleteMap;
    }
}
