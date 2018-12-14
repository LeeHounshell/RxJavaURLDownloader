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

    private volatile JobState jobState;
    private volatile List<String> urlList;
    private IJobQueue jobQueue;
    private Timer timer;

    private boolean isStarted = false;
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

    @Override
    public List<String> getUrlsForJob() {
        Log.d(TAG, "getUrlsForJob");
        return urlList;
    }

    @Override
    public boolean start() {
        Log.d(TAG, "start");
        if (jobState == JobState.JOB_COMPLETE) {
            Log.w(TAG, "start: job already COMPLETE");
            return false;
        }
        if (getNumberFailures() >= getNumberRetrys()) {
            Log.w(TAG, "=========> max retrys reached for job=" + this);
            retryTheJob();
            return false;
        }
        if (jobState == JobState.JOB_CREATED || (! isStarted && jobState == JobState.JOB_PAUSED)) {
            Log.d(TAG, "start: create Thread for job");
            isStarted = true;
            setJobState(Job.JobState.JOB_RUNNING);
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
            if (! isComplete()) {
                timer.cancel();
                if (!isPaused()) {
                    Log.d(TAG, "RerunJobTask: time to retry the job=" + this);
                    start();
                } else {
                    Log.d(TAG, "RerunJobTask: backoff the job=" + this);
                    retryAfterBackoff();
                }
            }
        }
    }

    private void retryAfterBackoff() {
        backoffSeconds *= 2; // use exponential backoff
        Log.d(TAG, "retryAfterBackoff: backoffSeconds=" + backoffSeconds);
        try {
            timer = new Timer();
            timer.schedule(new RerunJobTask(), backoffSeconds * 1000);
        }
        catch (Exception e) {
            Log.w(TAG, "unable to RerunJobTask: job=" + this + ", e=" + e);
        }
    }

    @Override
    public boolean pause() {
        Log.d(TAG, "pause");
        if (jobState == JobState.JOB_RUNNING || jobState == JobState.JOB_CREATED) {
            setJobState(Job.JobState.JOB_PAUSED);
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
            setJobState(JobState.JOB_RUNNING);
            if (! isStarted) {
                if (! start()) {
                    Log.w(TAG, "failed to start job=" + this);
                }
            }
            else {
                jobQueue.unpause();
            }
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
        synchronized (this) {
            return jobState == JobState.JOB_RUNNING;
        }
    }

    @Override
    public boolean isPaused() {
        synchronized (this) {
            return jobState == JobState.JOB_PAUSED;
        }
    }

    @Override
    public boolean isComplete() {
        synchronized (this) {
            return jobState == JobState.JOB_COMPLETE;
        }
    }

    @Override
    public boolean isCancelled() {
        synchronized (this) {
            return jobState == JobState.JOB_CANCELLED;
        }
    }

    public class NotifyJobStateChangeEvent {
        private int jobId = Job.this.getJobId();
        private JobState jobState = Job.this.jobState;

        public JobState getJobState() {
            return jobState;
        }

        public int getJobId() {
            return jobId;
        }

        public void setJobState(JobState jobState) {
            this.jobState = jobState;
        }

        @Override
        public String toString() {
            return "NotifyJobStateChangeEvent{" +
                    "jobId=" + jobId +
                    ", jobState=" + jobState +
                    '}';
        }
    }

    public void setJobState(JobState jobState) {
        synchronized (this) {
            this.jobState = jobState;
            NotifyJobStateChangeEvent notifyJobStateChangeEvent = new NotifyJobStateChangeEvent();
            EventBus.getDefault().post(notifyJobStateChangeEvent); // implement notify using EventBus
        }
    }

    public JobState getJobState() {
        synchronized (this) {
            return jobState;
        }
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

    @Override
    public void retryTheJob() {
        Log.d(TAG, "retryTheJob");
        if (! isComplete()) {
            incrementDownloadFailCount();
            if (getNumberFailures() >= getNumberRetrys()) {
                Log.w(TAG, "=========> cancel job after max retrys reached. job=" + this);
                cancel();
            } else {
                Log.w(TAG, "retry job after backoff");
                retryAfterBackoff();
            }
        }
        else {
            Log.d(TAG, "retryTheJob: already COMPLETE!");
        }
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
