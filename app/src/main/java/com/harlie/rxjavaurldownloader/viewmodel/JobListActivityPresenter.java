package com.harlie.rxjavaurldownloader.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.harlie.rxjavaurldownloader.BaseActivity;
import com.harlie.rxjavaurldownloader.R;
import com.harlie.rxjavaurldownloader.util.JobManagementDialog;
import com.harlie.urldownloaderlibrary.Job;
import com.harlie.urldownloaderlibrary.URLDownloader;


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
                    Toast.makeText(context, R.string.loading_please_wait, Toast.LENGTH_LONG).show();
                    new JobManagementDialog(baseActivity, title, detail, job, adapter);
                }
            });
        }
    }

    public void pauseAllJobs(View v) {
        Log.d(TAG, "pauseJobs: -click=");
        if (URLDownloader.getAllJobs() == null || URLDownloader.getAllJobs().size() == 0) {
            Toast.makeText(context, R.string.no_jobs_queued, Toast.LENGTH_SHORT).show();
        }
        else {
            URLDownloader.pauseJobs();
            Toast.makeText(context, R.string.jobs_paused, Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    }

    public void runAllJobs(View v) {
        Log.d(TAG, "runAllJobs");
        if (URLDownloader.getAllJobs() == null || URLDownloader.getAllJobs().size() == 0) {
            Toast.makeText(context, R.string.no_jobs_queued, Toast.LENGTH_SHORT).show();
        }
        else {
            URLDownloader.startJobs();
            Toast.makeText(context, R.string.jobs_running, Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    }

    public void stopAllJobs(View v) {
        Log.d(TAG, "stopAllJobs");
        if (URLDownloader.getAllJobs() == null || URLDownloader.getAllJobs().size() == 0) {
            Toast.makeText(context, R.string.no_jobs_queued, Toast.LENGTH_SHORT).show();
        }
        else {
            URLDownloader.stopJobs();
            Toast.makeText(context, R.string.jobs_stopped, Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
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
