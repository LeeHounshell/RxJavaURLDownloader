package com.harlie.urldownloaderlibrary;

import android.util.Log;

import java.util.List;


public class Job implements IJobInterface {
    static final String TAG = "LEE: " + Job.class.getSimpleName();

    public enum JobState {
        JOB_CREATED,
        JOB_QUEUED,
        JOB_RUNNING,
        JOB_PAUSED,
        JOB_COMPLETE,
    }

    private static volatile int sLastJobNumber;

    private int jobId;
    private List<String> urlList;
    private int timeOut;
    private int numberRetrys;
    private String callbackKey;
    private JobState jobState;
    private IJobQueue jobQueue;


    public Job(List<String> urlList, int timeOut, int numberRetrys, String callbackKey) {
        this.urlList = urlList;
        this.timeOut = timeOut;
        this.numberRetrys = numberRetrys;
        this.callbackKey = callbackKey;
        this.jobQueue = new JobQueueImpl();
        this.jobId = ++sLastJobNumber;
        this.jobState = JobState.JOB_CREATED;
    }

    public List<String> getUrlsForJob() {
        Log.d(TAG, "getUrlsForJob");
        return urlList;
    }

    @Override
    public void start() {
        Log.d(TAG, "start");
        jobQueue.start(this);
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause");
        jobQueue.pause();
    }

    @Override
    public void unpause() {
        Log.d(TAG, "unpause");
        jobQueue.unpause();
    }

    @Override
    public void cancel() {
        Log.d(TAG, "cancel");
        jobQueue.cancel();
    }

    @Override
    public boolean isCreated() {
        return jobState == JobState.JOB_CREATED;
    }

    @Override
    public boolean isQueued() {
        return jobState == JobState.JOB_QUEUED;
    }

    @Override
    public boolean isRunning() {
        return jobState == JobState.JOB_RUNNING;
    }

    @Override
    public boolean isPaused() {
        return jobState == JobState.JOB_PAUSED;
    }

    @Override
    public boolean isComplete() {
        return jobState == JobState.JOB_COMPLETE;
    }

    public void setJobState(JobState jobState) {
        this.jobState = jobState;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public int getNumberRetrys() {
        return numberRetrys;
    }

    public String getCallbackKey() {
        return callbackKey;
    }

    public int getJobId() {
        return jobId;
    }
}
