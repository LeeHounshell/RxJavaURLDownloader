package com.harlie.rxjavaurldownloader.viewmodel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.harlie.rxjavaurldownloader.BaseActivity;
import com.harlie.rxjavaurldownloader.databinding.AlbumUrlLayoutBinding;
import com.harlie.rxjavaurldownloader.model.AlbumUrl;

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
        final AlbumUrlLayoutBinding albumBinding = AlbumUrlLayoutBinding.inflate(layoutInflater, parent, false);
        albumBinding.setView(baseActivity);
        AlbumUrlViewHolder albumUrlViewHolder = new AlbumUrlViewHolder(albumBinding);
        MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(parent.getContext(), this, albumUrlViewHolder);
        albumBinding.setPresenter(mainActivityPresenter);
        return albumUrlViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumUrlViewHolder albumUrlViewHolder, int i) {
        Object obj = albumUrlList.get(i);
        albumUrlViewHolder.bind(obj);
    }

    @Override
    public int getItemCount() {
        return albumUrlList.size();
    }

    public List<AlbumUrl> getAlbumUrlList() {
        return albumUrlList;
    }
}
