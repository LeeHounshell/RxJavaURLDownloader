package com.harlie.rxjavaurldownloader;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.harlie.rxjavaurldownloader.databinding.ActivityJobListBinding;
import com.harlie.rxjavaurldownloader.viewmodel.JobListActivityPresenter;
import com.harlie.rxjavaurldownloader.viewmodel.JobListAdapter;
import com.harlie.urldownloaderlibrary.URLDownloader;


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

        JobListAdapter jobListAdapter = new JobListAdapter(this, URLDownloader.getAllJobs());
        jobListRecyclerView.setAdapter(jobListAdapter);
        JobListActivityPresenter jobListActivityPresenter = new JobListActivityPresenter(this, jobListAdapter);
        binding.setPresenter(jobListActivityPresenter);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
