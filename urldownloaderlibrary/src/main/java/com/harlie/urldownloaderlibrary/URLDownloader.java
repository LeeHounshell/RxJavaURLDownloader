package com.harlie.urldownloaderlibrary;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class URLDownloader {
    static final String TAG = "LEE: " + URLDownloader.class.getSimpleName();

    private static final String ACTION_START_JOBS = "com.harlie.rxjavaurldownloader.service.action.START_JOBS";
    private static final String ACTION_PAUSE_JOBS = "com.harlie.rxjavaurldownloader.service.action.PAUSE_JOBS";
    private static final String ACTION_STOP_JOBS = "com.harlie.rxjavaurldownloader.service.action.STOP_JOBS";

    private static URLDownloader sInstance;
    private volatile List<Job> jobList = new ArrayList<Job>();


    public static URLDownloader getInstance() {
        if (sInstance == null) {
            sInstance = new URLDownloader();
        }
        return sInstance;
    }

    private URLDownloader() {
    }

    public void requestDownloadOperation(final Intent intent) {
        Log.d(TAG, "requestDownloadOperation: intent=" + intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (intent.getAction().equals(ACTION_START_JOBS)) {
                    startJobs();
                }
                else if (intent.getAction().equals(ACTION_PAUSE_JOBS)) {
                    pauseJobs();
                }
                else if (intent.getAction().equals(ACTION_STOP_JOBS)) {
                    stopJobs();
                }
            }
        }).start();
    }

    public Job createJob(List<String> urlList, int timeOut, int numberRetrys, int callbackKey) {
        Log.d(TAG, "createJob: urlList=" + urlList + ", timeOut=" + timeOut + ", numberRetrys=" + numberRetrys + ", callbackKey=" + callbackKey);
        Job job = new Job(urlList, timeOut, numberRetrys, callbackKey);
        if (job != null) {
            synchronized (this) {
                jobList.add(job);
            }
        }
        else {
            Log.e(TAG, "createJob for " + urlList.size() + " Urls failed! timeout=" + timeOut + ", numberRetrys=" + numberRetrys + ", callbackKey=" + callbackKey);
        }
        return job;
    }

    public List<Job> getAllJobs() {
        Log.d(TAG, "getAllJobs");
        return jobList;
    }

    public List<String> getUrlsForJob(Job job) {
        Log.d(TAG, "getUrlsForJob");
        Job theJob = getJob(job.getJobId());
        if (theJob == null) {
            return null;
        }
        return theJob.getUrlsForJob();
    }

    public void startJobs() {
        Log.d(TAG, "startJobs");
        for (Job job : jobList) {
            if (! job.isRunning() && ! job.isCancelled() && ! job.isComplete()) {
                Log.d(TAG, "startJobs: job.start");
                job.start();
            }
            if (job.isPaused()) {
                Log.d(TAG, "startJobs: job.unpause");
                job.unpause();
            }
        }
    }

    public void pauseJobs() {
        Log.d(TAG, "pauseJobs");
        for (Job job : jobList) {
            if (job.isRunning() || job.isCreated()) {
                job.pause();
            }
        }
    }

    public void stopJobs() {
        Log.d(TAG, "stopJobs");
        for (Job job : jobList) {
            if (job.isRunning() || job.isCreated() || job.isPaused()) {
                job.cancel();
            }
        }
    }

    public Job getJob(int jobId) {
        for (Job job : jobList) {
            if (job.getJobId() == jobId) {
                return job;
            }
        }
        return null;
    }
}
