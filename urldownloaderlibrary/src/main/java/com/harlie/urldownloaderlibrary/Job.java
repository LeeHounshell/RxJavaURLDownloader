package com.harlie.urldownloaderlibrary;

import android.content.Context;
import android.content.res.Resources;
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
        JOB_CANCELLED
    }

    private static volatile int sLastJobNumber;

    private int jobId;
    private List<String> urlList;
    private int timeOut;
    private int numberRetrys;
    private int callbackKey;
    private JobState jobState;
    private IJobQueue jobQueue;


    public Job(List<String> urlList, int timeOut, int numberRetrys, int callbackKey) {
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

    @Override
    public boolean isCancelled() {
        return jobState == JobState.JOB_CANCELLED;
    }

    public void setJobState(JobState jobState) {
        this.jobState = jobState;
    }

    public JobState getJobState() {
        return jobState;
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

    public int getCallbackKey() {
        return callbackKey;
    }

    public int getJobId() {
        return jobId;
    }

    public String getJobName(Context context) {
        String name = "";
        if (context != null) {
            Resources resources = context.getResources();
            name = resources.getString(R.string.job_id) + ": " + jobId;
        }
        return name;
    }

    public String getJobInfo(Context context, boolean alsoGetJobName) {
        String info = "";
        if (context != null) {
            Resources resources = context.getResources();
            Job job = URLDownloader.getJob(jobId);
            if (alsoGetJobName) {
                info = getJobName(context) + "\n";
            }
            info = info +
                       resources.getString(R.string.job_size) + ": " + job.getUrlsForJob().size() +
                "\n" + resources.getString(R.string.job_timeout) + ": " + job.getTimeOut() +
                "\n" + resources.getString(R.string.job_retrys) + ": " + job.getNumberRetrys() +
                "\n" + resources.getString(R.string.job_status) + ": " + job.getJobState().name();
        }
        return info;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobId=" + jobId +
                ", urlList=" + urlList +
                ", timeOut=" + timeOut +
                ", numberRetrys=" + numberRetrys +
                ", callbackKey='" + callbackKey + '\'' +
                ", jobState=" + jobState +
                ", jobQueue=" + jobQueue +
                '}';
    }
}
