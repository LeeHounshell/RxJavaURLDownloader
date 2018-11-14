package com.harlie.rxjavaurldownloader.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.harlie.rxjavaurldownloader.BaseActivity;
import com.harlie.rxjavaurldownloader.JobListActivity;
import com.harlie.rxjavaurldownloader.R;
import com.harlie.rxjavaurldownloader.service.UrlDownloaderRequestDownloadService;
import com.harlie.rxjavaurldownloader.util.JobManagementDialog;
import com.harlie.urldownloaderlibrary.Job;
import com.harlie.urldownloaderlibrary.URLDownloader;

import org.greenrobot.eventbus.Subscribe;


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

    public void doClick(final Job job) {
        Log.d(TAG, "doClick: -click-");
        if (context instanceof BaseActivity) {
            Log.d(TAG, "doClick: job=" + job);
            final BaseActivity baseActivity = (BaseActivity) context;
            final String title = baseActivity.getResources().getString(R.string.job_dialog_title);
            final String detail = job.getJobInfo(context, true);
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new JobManagementDialog(baseActivity, title, detail, job, adapter);
                }
            });
        }
    }

    public void pauseAllJobs(View v) {
        Log.d(TAG, "pauseJobs: -click=");
        UrlDownloaderRequestDownloadService.startActionPauseJobs(context);
    }

    public void runAllJobs(View v) {
        Log.d(TAG, "runAllJobs");
        UrlDownloaderRequestDownloadService.startActionStartJobs(context);
    }

    public void stopAllJobs(View v) {
        Log.d(TAG, "stopAllJobs");
        UrlDownloaderRequestDownloadService.startActionStopJobs(context);
    }

    @Subscribe
    public void onEvent(UrlDownloaderRequestDownloadService.NotifyDataSetEvent notifyEvent) {
        Log.d(TAG, "---------> onEvent <--------- notifyEvent=" + notifyEvent);
        if (context instanceof JobListActivity) {
            JobListActivity jobListActivity = (JobListActivity) context;
            jobListActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onEvent: got service notifyEvent");
                    adapter.notifyDataSetChanged();
                }
            });
        }
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
            case JOB_CANCELLED:
                color = resources.getColor(R.color.color_JOB_CANCELLED);
                break;
        }
        return color;
    }

    public Context getContext() {
        return context;
    }
}
