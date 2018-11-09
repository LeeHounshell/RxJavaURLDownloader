package com.harlie.rxjavaurldownloader.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.harlie.rxjavaurldownloader.model.AlbumUrl;
import com.harlie.rxjavaurldownloader.databinding.JobAlbumUrlLayoutBinding;

import java.util.List;


public class AlbumUrlAdapter extends RecyclerView.Adapter<AlbumUrlViewHolder> {
    static final String TAG = "LEE: " + AlbumUrlAdapter.class.getSimpleName();

    Context context;
    List<AlbumUrl> albumUrlList;

    public AlbumUrlAdapter(Context context, List<AlbumUrl> albumUrlList) {
        this.context = context;
        this.albumUrlList = albumUrlList;
    }

    public AlbumUrlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        JobAlbumUrlLayoutBinding albumBinding = JobAlbumUrlLayoutBinding.inflate(layoutInflater, parent, false);
        return new AlbumUrlViewHolder(albumBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumUrlViewHolder albumUrlViewHolder, int i) {
        Object obj = albumUrlList.get(i);
        albumUrlViewHolder.bind(obj);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + albumUrlList.size());
        return albumUrlList.size();
    }
}
