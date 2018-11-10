package com.harlie.rxjavaurldownloader.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.harlie.rxjavaurldownloader.R;
import com.harlie.urldownloaderlibrary.Job;


public class JobListActivityPresenter {
    static final String TAG = "LEE: " + JobListActivityPresenter.class.getSimpleName();

    private static int sCallback;

    private Context context;
    private JobListAdapter adapter;
    private JobListViewHolder jobListViewHolder;


    public JobListActivityPresenter(Context context, JobListAdapter adapter, JobListViewHolder jobListViewHolder) {
        this.context = context;
        this.adapter = adapter;
        this.jobListViewHolder = jobListViewHolder;
    }

    public JobListActivityPresenter(Context context, JobListAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        this.jobListViewHolder = null;
    }

    public void doClick(Job job) {
        Log.d(TAG, "-click-" + job);
    }

    public int getBackgroundColor(Job job) {
        Resources resources = context.getResources();
        int color = 0;
        switch (job.getJobState()) {
            case JOB_CREATED:
                color = resources.getColor(R.color.color_JOB_CREATED);
                break;
            case JOB_QUEUED:
                color = resources.getColor(R.color.color_JOB_QUEUED);
                break;
            case JOB_RUNNING:
                color = resources.getColor(R.color.color_JOB_RUNNING);
                break;
            case JOB_PAUSED:
                color = resources.getColor(R.color.color_JOB_PAUSED);
                break;
            case JOB_COMPLETE:
                color = resources.getColor(R.color.color_JOB_COMPLETE);
                break;
        }
        return color;
    }

    public Context getContext() {
        return context;
    }
}
