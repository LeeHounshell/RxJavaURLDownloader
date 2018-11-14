package com.harlie.urldownloaderlibrary;


import java.util.Map;

public interface IJobQueue {
    void start(Object lock);
    void pause();
    void unpause();
    void cancel();
    void complete();
    Job.JobState getJobState();
    Map<String, UrlResult> getUrlResultMap();
    Map<String, UrlResult> getUrlCompleteMap();
}
