package com.harlie.urldownloaderlibrary;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class URLDownloader {
    static final String TAG = "LEE: " + URLDownloader.class.getSimpleName();

    private static URLDownloader sInstance;
    private List<Job> jobList = new ArrayList<Job>();


    public static URLDownloader getInstance() {
        if (sInstance == null) {
            sInstance = new URLDownloader();
        }
        return sInstance;
    }

    private URLDownloader() {
    }

    public Job createJob(List<String> urlList, int timeOut, int numberRetrys, int callbackKey) {
        Log.d(TAG, "createJob: urlList=" + urlList + ", timeOut=" + timeOut + ", numberRetrys=" + numberRetrys + ", callbackKey=" + callbackKey);
        Job job = new Job(urlList, timeOut, numberRetrys, callbackKey);
        if (job != null) {
            jobList.add(job);
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
                job.start();
            }
        }
    }

    public void pauseJobs() {
        Log.d(TAG, "pauseJobs");
        for (Job job : jobList) {
            if (job.isRunning()) {
                job.pause();
            }
        }
    }

    public void stopJobs() {
        Log.d(TAG, "stopJobs");
        for (Job job : jobList) {
            if (job.isRunning() || job.isPaused() || job.isQueued()) {
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
