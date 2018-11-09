package com.harlie.rxjavaurldownloader.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.harlie.rxjavaurldownloader.BR;
import com.harlie.rxjavaurldownloader.databinding.JobAlbumUrlLayoutBinding;


public class AlbumUrlViewHolder extends RecyclerView.ViewHolder {
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
}
