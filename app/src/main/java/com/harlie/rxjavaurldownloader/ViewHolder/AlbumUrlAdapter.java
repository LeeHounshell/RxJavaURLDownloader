package com.harlie.rxjavaurldownloader.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harlie.rxjavaurldownloader.Model.AlbumUrl;
import com.harlie.rxjavaurldownloader.R;

import java.util.List;


public class AlbumUrlAdapter extends RecyclerView.Adapter<AlbumUrlViewHolder> {
    static final String TAG = "LEE: " + AlbumUrlAdapter.class.getSimpleName();

    Context context;
    List<AlbumUrl> albumUrlList;

    public AlbumUrlAdapter(Context context, List<AlbumUrl> albumUrlList) {
        this.context = context;
        this.albumUrlList = albumUrlList;
    }

    @NonNull
    @Override
    public AlbumUrlViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.job_album_url_layout, viewGroup, false);
        return new AlbumUrlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumUrlViewHolder albumUrlViewHolder, int i) {
        String title = albumUrlList.get(i).getAlbumTitle();
        String photoUrl = albumUrlList.get(i).getAlbumPhotoUrl();
        String thumbUrl = albumUrlList.get(i).getAlbumThumbnailUrl();
        Log.d(TAG, "onBindViewHolder: title=" + title + ", photoUrl=" + photoUrl + ", thumbUrl=" + thumbUrl);
        albumUrlViewHolder.getAlbumTitle().setText(title);
        albumUrlViewHolder.getAlbumPhotoUrl().setText(photoUrl);
        albumUrlViewHolder.getAlbumThumbUrl().setText(thumbUrl);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + albumUrlList.size());
        return albumUrlList.size();
    }
}
