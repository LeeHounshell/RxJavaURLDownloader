package com.harlie.urldownloaderlibrary;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class URLDownloader {
    static final String TAG = "LEE: " + URLDownloader.class.getSimpleName();

    private static URLDownloader sInstance;
    private static List<Job> jobList = new ArrayList<Job>();


    public static URLDownloader getInstance() {
        if (sInstance == null) {
            sInstance = new URLDownloader();
        }
        return sInstance;
    }

    private URLDownloader() {
    }

    public static Job createJob(List<String> urlList, int timeOut, int numberRetrys, String callbackKey) {
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

    public static List<Job> getAllJobs() {
        Log.d(TAG, "getAllJobs");
        return jobList;
    }

    public static List<String> getUrlsForJob(Job job) {
        Log.d(TAG, "getUrlsForJob");
        Job theJob = findJob(job);
        if (theJob == null) {
            return null;
        }
        return theJob.getUrlsForJob();
    }

    public static void startJobs() {
        Log.d(TAG, "startJobs");
        for (Job job : jobList) {
            if (! job.isRunning()) {
                job.start();
            }
        }
    }

    public static void pauseJobs() {
        Log.d(TAG, "pauseJobs");
        for (Job job : jobList) {
            if (job.isRunning()) {
                job.pause();
            }
        }
    }

    private static Job findJob(Job myJob) {
        for (Job job : jobList) {
            if (job.equals(myJob)) {
                return job;
            }
        }
        return null;
    }
}
