package com.harlie.rxjavaurldownloader;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.harlie.rxjavaurldownloader.databinding.ActivityJobListBinding;
import com.harlie.rxjavaurldownloader.service.UrlDownloaderRequestDownloadService;
import com.harlie.rxjavaurldownloader.viewmodel.JobListActivityPresenter;
import com.harlie.rxjavaurldownloader.viewmodel.JobListAdapter;
import com.harlie.urldownloaderlibrary.Job;
import com.harlie.urldownloaderlibrary.URLDownloader;
import com.harlie.urldownloaderlibrary.UrlResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Iterator;
import java.util.Map;


public class JobListActivity extends BaseActivity {
    static final String TAG = "LEE: " + JobListActivity.class.getSimpleName();

    ActivityJobListBinding binding;
    RecyclerView jobListRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_list);

        jobListRecyclerView = findViewById(R.id.job_list_recyclerview);
        jobListRecyclerView.setHasFixedSize(true);
        jobListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        JobListAdapter jobListAdapter = new JobListAdapter(this, URLDownloader.getInstance().getAllJobs());
        jobListRecyclerView.setAdapter(jobListAdapter);
        JobListActivityPresenter jobListActivityPresenter = new JobListActivityPresenter(this, jobListAdapter);
        binding.setPresenter(jobListActivityPresenter);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(UrlDownloaderRequestDownloadService.NotifyDataSetEvent notifyEvent) {
        Log.d(TAG, "---------> onEvent <--------- notifyEvent action=" + notifyEvent.getAction());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onEvent: NotifyDataSetEvent notifyDataSetChanged");
                JobListAdapter jobListAdapter = (JobListAdapter) jobListRecyclerView.getAdapter();
                jobListAdapter.setJobList(URLDownloader.getInstance().getAllJobs());
                jobListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void onEvent(Job.URLDownloaderJobCompletionEvent completionEvent) {
        Log.d(TAG, "---------> onEvent <--------- completionEvent=" + completionEvent);
        JobListActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onEvent: JobCompletionEvent notifyDataSetChanged");
                JobListAdapter jobListAdapter = (JobListAdapter) jobListRecyclerView.getAdapter();
                jobListAdapter.setJobList(URLDownloader.getInstance().getAllJobs());
                jobListAdapter.notifyDataSetChanged();
            }
        });
        Log.d(TAG, "job=" + completionEvent.getJob());
        Iterator it = completionEvent.getResultMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String key = (String) pair.getKey();
            UrlResult result = (UrlResult) pair.getValue();
            Log.d(TAG, "COMPLETE: url=" + key);
            Log.d(TAG, "COMPLETE: UrlResult=" + result);
        }
    }

    @Subscribe
    public void onEvent(Job.NotifyJobStateChangeEvent stateChangeEvent) {
        Log.d(TAG, "---------> onEvent <--------- stateChangeEvent=" + stateChangeEvent.getJobState());
        JobListActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onEvent: StateChangeEvent notifyDataSetChanged");
                JobListAdapter jobListAdapter = (JobListAdapter) jobListRecyclerView.getAdapter();
                jobListAdapter.setJobList(URLDownloader.getInstance().getAllJobs());
                jobListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
