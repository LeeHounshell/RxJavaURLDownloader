package com.harlie.urldownloaderlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class Job implements IJobInterface {
    static final String TAG = "LEE: " + Job.class.getSimpleName();

    public enum JobState {
        JOB_CREATED,
        JOB_RUNNING,
        JOB_PAUSED,
        JOB_COMPLETE,
        JOB_CANCELLED
    }

    private static volatile int sLastJobNumber = 0;

    private JobState jobState;
    private List<String> urlList;
    private IJobQueue jobQueue;
    private Timer timer;

    private int jobId;
    private int timeOut;
    private int numberRetrys;
    private int numberFailures;
    private int callbackKey;
    private int backoffSeconds = 2;
    private final Object jobLock = new Object();


    public Job(List<String> urlList, int timeOut, int numberRetrys, int callbackKey) {
        this.urlList = urlList;
        this.timeOut = timeOut;
        this.numberRetrys = numberRetrys;
        this.numberFailures = 0;
        this.callbackKey = callbackKey;
        this.jobId = ++sLastJobNumber;
        this.jobState = JobState.JOB_CREATED;
        this.jobQueue = new JobQueueImpl(this);
    }

    public List<String> getUrlsForJob() {
        Log.d(TAG, "getUrlsForJob");
        return urlList;
    }

    @Override
    public boolean start() {
        Log.d(TAG, "start");
        if (getNumberFailures() >= getNumberRetrys()) {
            Log.w(TAG, "max retrys reached for job=" + this);
            return false;
        }
        if (jobState == JobState.JOB_CREATED) {
            Log.d(TAG, "start: create Thread for job");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jobQueue.start(jobLock);
                }
            }).start();
            waitForJobStartup();
            return true;
        }
        else if (jobState == JobState.JOB_PAUSED) {
            Log.d(TAG, "start: unpause job");
            jobQueue.unpause();
        }
        else {
            Log.e(TAG, "got Job start() but job is not in state JOB_CREATED or JOB_PAUSED");
        }
        return false;
    }

    private void waitForJobStartup() {
        Log.d(TAG, "waitForJobStartup");
        while (jobState == JobState.JOB_CREATED) {
            try {
                Log.d(TAG, "waitForJobStartup: wait");
                synchronized (jobLock) {
                    jobLock.wait();
                }
                Log.d(TAG, "waitForJobStartup: resume execution");
            } catch (InterruptedException e) {
                Log.e(TAG, "INTERRUPTED: waitForJobStartup, e=" + e);
            }
        }
        Log.d(TAG, "waitForJobStartup: done");
    }

    class RerunJobTask extends TimerTask {
        public void run() {
            timer.cancel();
            Log.d(TAG, "time to retry the job=" + this);
            start();
        }
    }

    public void retryAfterBackoff() {
        backoffSeconds *= 2; // use exponential backoff
        Log.d(TAG, "retryAfterBackoff: backoffSeconds=" + backoffSeconds);
        timer = new Timer();
        timer.schedule(new RerunJobTask(), backoffSeconds * 1000);
    }

    @Override
    public boolean pause() {
        Log.d(TAG, "pause");
        if (jobState == JobState.JOB_RUNNING) {
            jobQueue.pause();
            return true;
        }
        else {
            Log.e(TAG, "got Job pause() but job is not in state JOB_RUNNING");
        }
        return false;
    }

    @Override
    public boolean unpause() {
        Log.d(TAG, "unpause");
        if (jobState == JobState.JOB_PAUSED) {
            jobQueue.unpause();
            return true;
        }
        else {
            Log.e(TAG, "got Job unpause() but job is not in state JOB_PAUSED");
        }
        return false;
    }

    public class URLDownloaderJobCompletionEvent {
        private Job job;
        private Map<String, UrlResult> resultMap;

        public URLDownloaderJobCompletionEvent(Job job, Map<String, UrlResult> resultMap) {
            this.job = job;
            this.resultMap = resultMap;
        }

        public Job getJob() {
            return job;
        }

        public Map<String, UrlResult> getResultMap() {
            return resultMap;
        }
    }

    @Override
    public boolean complete() {
        Log.d(TAG, "===> complete <===");
        Map<String, UrlResult> resultMap = jobQueue.getUrlResultMap();
        resultMap.putAll(jobQueue.getUrlCompleteMap()); // overlay with SHA-1 completion
        URLDownloaderJobCompletionEvent completionEvent = new URLDownloaderJobCompletionEvent(Job.this, resultMap);
        EventBus.getDefault().post(completionEvent); // implement callback using EventBus
        return true;
    }

    @Override
    public boolean cancel() {
        Log.d(TAG, "cancel");
        if (jobState != JobState.JOB_COMPLETE && jobState != JobState.JOB_CANCELLED) {
            jobQueue.cancel();
            return true;
        }
        else {
            Log.e(TAG, "got Job cancel() but job is already JOB_COMPLETE or JOB_CANCELLED");
        }
        return false;
    }

    @Override
    public boolean isCreated() {
        return jobState == JobState.JOB_CREATED;
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

    public int getNumberFailures() {
        return numberFailures;
    }

    public void incrementDownloadFailCount() {
        ++numberFailures;
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

    public IJobQueue getJobQueue() {
        Log.d(TAG, "getJobQueue");
        return jobQueue;
    }

    public String getJobInfo(Context context, boolean alsoGetJobName) {
        String info = "";
        if (context != null) {
            Resources resources = context.getResources();
            Job job = URLDownloader.getInstance().getJob(jobId);
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
