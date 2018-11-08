package com.harlie.rxjavaurldownloader.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.harlie.rxjavaurldownloader.R;


public class AlbumUrlViewHolder extends RecyclerView.ViewHolder {
    private TextView albumTitle;
    private TextView albumPhotoUrl;
    private TextView albumThumbUrl;

    public AlbumUrlViewHolder(@NonNull View itemView) {
        super(itemView);
        albumTitle = itemView.findViewById(R.id.album_title);
        albumPhotoUrl = itemView.findViewById(R.id.album_photo_url);
        albumThumbUrl = itemView.findViewById(R.id.album_thumb_url);
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
