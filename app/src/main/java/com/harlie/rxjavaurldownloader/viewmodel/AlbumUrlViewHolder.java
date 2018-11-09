package com.harlie.rxjavaurldownloader.viewmodel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.harlie.rxjavaurldownloader.BR;
import com.harlie.rxjavaurldownloader.BaseActivity;
import com.harlie.rxjavaurldownloader.databinding.JobAlbumUrlLayoutBinding;


public class AlbumUrlViewHolder extends RecyclerView.ViewHolder {
    static final String TAG = "LEE: " + AlbumUrlViewHolder.class.getSimpleName();

    private final JobAlbumUrlLayoutBinding binding;
    private TextView albumTitle;
    private TextView albumPhotoUrl;
    private TextView albumThumbUrl;

    public AlbumUrlViewHolder(@NonNull JobAlbumUrlLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Object obj) {
        binding.setVariable(BR.obj, obj);
        binding.executePendingBindings();
    }

    public TextView getAlbumTitle() {
        return albumTitle;
    }

    public TextView getAlbumPhotoUrl() {
        return albumPhotoUrl;
    }

    public TextView getAlbumThumbUrl() {
        return albumThumbUrl;
    }

    @Override
    public String toString() {
        return "AlbumUrlViewHolder{" +
                "albumTitle=" + albumTitle == null ? "null" : albumTitle.getText().toString() +
                ", albumPhotoUrl=" + albumPhotoUrl == null ? "null" : albumPhotoUrl.getText().toString() +
                ", albumThumbUrl=" + albumThumbUrl == null ? "null" : albumThumbUrl.getText().toString() +
                '}';
    }
}
