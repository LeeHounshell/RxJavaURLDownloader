package com.harlie.urldownloaderlibrary;

public interface IJobInterface {
    void start();
    void pause();
    void unpause();
    void cancel();
    boolean isCreated();
    boolean isQueued();
    boolean isRunning();
    boolean isPaused();
    boolean isComplete();
    boolean isCancelled();
}
