package com.harlie.rxjavaurldownloader.viewmodel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.harlie.rxjavaurldownloader.BR;
import com.harlie.rxjavaurldownloader.databinding.AlbumUrlLayoutBinding;


public class AlbumUrlViewHolder extends RecyclerView.ViewHolder {
    static final String TAG = "LEE: " + AlbumUrlViewHolder.class.getSimpleName();

    private final AlbumUrlLayoutBinding binding;

    public AlbumUrlViewHolder(@NonNull AlbumUrlLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Object obj) {
        binding.setVariable(BR.obj, obj);
        binding.executePendingBindings();
    }
}
