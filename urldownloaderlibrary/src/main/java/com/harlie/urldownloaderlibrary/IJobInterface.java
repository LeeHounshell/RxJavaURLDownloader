package com.harlie.urldownloaderlibrary;

public interface IJobInterface {
    boolean start();
    boolean pause();
    boolean unpause();
    boolean cancel();
    boolean complete();

    boolean isCreated();
    boolean isRunning();
    boolean isPaused();
    boolean isComplete();
    boolean isCancelled();
}
