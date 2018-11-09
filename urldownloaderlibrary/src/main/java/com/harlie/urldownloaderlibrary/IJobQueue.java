package com.harlie.urldownloaderlibrary;

interface IJobQueue {
    void start(Job job);
    void pause();
    void unpause();
    void cancel();
    Job.JobState getJobState();
}
