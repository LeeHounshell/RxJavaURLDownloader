package com.harlie.urldownloaderlibrary;

import android.util.Log;


class JobQueueImpl implements IJobQueue {
    static final String TAG = "LEE: " + JobQueueImpl.class.getSimpleName();

    private Job job;

    @Override
    public void start(Job job) {
        Log.d(TAG, "start: job=" + job);
        this.job = job;
        job.setJobState(Job.JobState.JOB_RUNNING);
        // FIXME
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause: job=" + job);
        job.setJobState(Job.JobState.JOB_PAUSED);
        // FIXME
    }

    @Override
    public void unpause() {
        Log.d(TAG, "unpause: job=" + job);
        job.setJobState(Job.JobState.JOB_RUNNING);
        // FIXME
    }

    @Override
    public void cancel() {
        Log.d(TAG, "cancel: job=" + job);
        job.setJobState(Job.JobState.JOB_CANCELLED);
        // FIXME
    }

    @Override
    public Job.JobState getJobState() {
        return null;
    }
}
