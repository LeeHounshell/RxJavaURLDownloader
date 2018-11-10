package com.harlie.rxjavaurldownloader.viewmodel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.harlie.rxjavaurldownloader.BaseActivity;
import com.harlie.rxjavaurldownloader.databinding.JobListLayoutBinding;
import com.harlie.urldownloaderlibrary.Job;

import java.util.List;


public class JobListAdapter extends RecyclerView.Adapter<JobListViewHolder> {
    static final String TAG = "LEE: " + JobListAdapter.class.getSimpleName();

    BaseActivity baseActivity;
    List<Job> jobList;

    public JobListAdapter(BaseActivity baseActivity, List<Job> jobList) {
        this.baseActivity = this.baseActivity;
        this.jobList = jobList;
    }

    public JobListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final JobListLayoutBinding jobListBinding = JobListLayoutBinding.inflate(layoutInflater, parent, false);
        jobListBinding.setView(baseActivity);
        JobListViewHolder jobListViewHolder = new JobListViewHolder(jobListBinding);
        JobListActivityPresenter jobListActivityPresenter = new JobListActivityPresenter(parent.getContext(), this, jobListViewHolder);
        jobListBinding.setPresenter(jobListActivityPresenter);
        return jobListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JobListViewHolder jobListViewHolder, int i) {
        Object obj = jobList.get(i);
        jobListViewHolder.bind(obj);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public List<Job> getJobList() {
        return jobList;
    }
}
