package com.harlie.rxjavaurldownloader.viewmodel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.harlie.rxjavaurldownloader.BR;
import com.harlie.rxjavaurldownloader.databinding.JobListLayoutBinding;


public class JobListViewHolder extends RecyclerView.ViewHolder {
    static final String TAG = "LEE: " + JobListViewHolder.class.getSimpleName();

    private final JobListLayoutBinding binding;

    public JobListViewHolder(@NonNull JobListLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Object obj) {
        binding.setVariable(BR.obj, obj);
        binding.executePendingBindings();
    }
}
