package com.harlie.rxjavaurldownloader.util;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.harlie.rxjavaurldownloader.BaseActivity;
import com.harlie.rxjavaurldownloader.R;
import com.harlie.rxjavaurldownloader.viewmodel.JobListAdapter;
import com.harlie.urldownloaderlibrary.Job;

import org.greenrobot.eventbus.EventBus;


public class JobManagementDialog
{
    private final static String TAG = "LEE: " + JobManagementDialog.class.getSimpleName();

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alert;
    private BaseActivity baseActivity;
    private Job job;
    private JobListAdapter adapter;
    private View dialogView;

    public JobManagementDialog(BaseActivity activity, String title, String message, final Job job, JobListAdapter adapter)
    {
        Log.v(TAG, "JobManagementDialog");
        activity.findViewById(R.id.loading_panel).setVisibility(View.VISIBLE);

        this.job = job;
        this.baseActivity = activity;
        this.adapter = adapter;

        final ViewGroup nullParent = null;
        dialogView = baseActivity.getLayoutInflater().inflate(R.layout.job_management_dialog, nullParent);
        adjustDialogButtons(dialogView, job);

        final TextView urlListTextView = dialogView.findViewById(R.id.the_url_list);
        StringBuilder urlList = new StringBuilder();
        int count = 0;
        for (String url : job.getUrlList()) {
            if (count >= 100) {
                Log.d(TAG, "only show the first 100 URLs");
                Toast.makeText(activity, R.string.only_show_100, Toast.LENGTH_LONG).show();
                break;
            }
            urlList.append("" + ++count + ": " + url + "\n");
        }
        urlListTextView.setText(urlList.toString());

        alertDialogBuilder = new AlertDialog.Builder(baseActivity);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(baseActivity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.d(TAG, "onClick: OK");
                        alert.dismiss();
                        dialogView.setVisibility(View.GONE);
                        String textMessage = urlListTextView.getText().toString();
                        JobChangedEvent jobChangedEvent = new JobChangedEvent(job, job.getCallbackKey());
                        jobChangedEvent.post();
                    }
                });

        alert = alertDialogBuilder.create();
        alert.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                baseActivity.findViewById(R.id.loading_panel).setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void adjustDialogButtons(View dialog, final Job job) {
        Log.d(TAG, "adjustDialogButtons");
        Button runPauseResumeJobButton = dialog.findViewById(R.id.run_pause_resume_job_button);
        Button cancelJobButton = dialog.findViewById(R.id.cancel_job_button);
        switch (job.getJobState()) {
            case JOB_CREATED:
                runPauseResumeJobButton.setText(dialog.getResources().getString(R.string.run_job));
                cancelJobButton.setVisibility(View.GONE);
                break;
            case JOB_QUEUED:
                runPauseResumeJobButton.setText(dialog.getResources().getString(R.string.run_job));
                cancelJobButton.setText(dialog.getResources().getString(R.string.cancel_job));
                break;
            case JOB_RUNNING:
                runPauseResumeJobButton.setText(dialog.getResources().getString(R.string.pause_job));
                cancelJobButton.setText(dialog.getResources().getString(R.string.cancel_job));
                break;
            case JOB_PAUSED:
                runPauseResumeJobButton.setText(dialog.getResources().getString(R.string.resume_job));
                cancelJobButton.setText(dialog.getResources().getString(R.string.cancel_job));
                break;
            case JOB_COMPLETE:
                runPauseResumeJobButton.setVisibility(View.GONE);
                cancelJobButton.setVisibility(View.GONE);
                break;
            case JOB_CANCELLED:
                runPauseResumeJobButton.setVisibility(View.GONE);
                cancelJobButton.setVisibility(View.GONE);
                break;
        }
        runPauseResumeJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "runPauseResumeJobButton: -click-");
                alert.dismiss();
                dialogView.setVisibility(View.GONE);
                runPauseResumeJob(v);
            }
        });
        cancelJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancelJobButton: -click-");
                alert.dismiss();
                dialogView.setVisibility(View.GONE);
                cancelJob(v);
            }
        });
    }

    public void runPauseResumeJob(View v) {
        Log.d(TAG, "runPauseResumeJob: -click-");
        switch (job.getJobState()) {
            case JOB_CREATED:
            case JOB_QUEUED:
                Log.d(TAG, "runPauseResumeJob: run job");
                job.start();
                break;
            case JOB_RUNNING:
                Log.d(TAG, "runPauseResumeJob: pause job");
                job.pause();
                break;
            case JOB_PAUSED:
                Log.d(TAG, "runPauseResumeJob: resume job");
                job.unpause();
                break;
        }
        notifyDataSetChanged();
    }

    public void cancelJob(View v) {
        Log.d(TAG, "cancelJob: -click-");
        if (job.getJobState() != Job.JobState.JOB_COMPLETE
                && job.getJobState() != Job.JobState.JOB_CANCELLED) {
            Log.d(TAG, "cancelling the job");
            job.cancel();
            notifyDataSetChanged();
        }
    }

    private void notifyDataSetChanged() {
        if (adapter != null) {
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "notifyDataSetChanged");
                    adapter.notifyDataSetChanged();
                }
            });
        }
        else {
            Log.w(TAG, "notifyDataSetChanged: the adapter is null!");
        }
    }

    public static class JobChangedEvent {
        private Job job;
        private int callbackId;

        JobChangedEvent(Job job, int callbackId) {
            this.job = job;
            this.callbackId = callbackId;
        }

        public void post() {
            Log.v(TAG, "post: job=" + job);
            EventBus.getDefault().post(new JobChangedEvent(job, callbackId));
        }

        @Override
        public String toString() {
            return "JobChangedEvent{" +
                    "job='" + job + '\'' +
                    "callbackId='" + callbackId + '\'' +
                    '}';
        }
    }

}
