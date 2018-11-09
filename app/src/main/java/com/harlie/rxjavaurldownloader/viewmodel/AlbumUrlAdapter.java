package com.harlie.rxjavaurldownloader.viewmodel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.harlie.rxjavaurldownloader.BaseActivity;
import com.harlie.rxjavaurldownloader.MainActivity;
import com.harlie.rxjavaurldownloader.model.AlbumUrl;
import com.harlie.rxjavaurldownloader.databinding.JobAlbumUrlLayoutBinding;

import java.util.List;


public class AlbumUrlAdapter extends RecyclerView.Adapter<AlbumUrlViewHolder> {
    static final String TAG = "LEE: " + AlbumUrlAdapter.class.getSimpleName();

    BaseActivity baseActivity;
    List<AlbumUrl> albumUrlList;

    public AlbumUrlAdapter(BaseActivity baseActivity, List<AlbumUrl> albumUrlList) {
        this.baseActivity = this.baseActivity;
        this.albumUrlList = albumUrlList;
    }

    public AlbumUrlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final JobAlbumUrlLayoutBinding albumBinding = JobAlbumUrlLayoutBinding.inflate(layoutInflater, parent, false);
        albumBinding.setView(baseActivity);
        MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this, parent.getContext());
        albumBinding.setPresenter(mainActivityPresenter);
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
