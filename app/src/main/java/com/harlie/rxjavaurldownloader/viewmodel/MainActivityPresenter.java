package com.harlie.rxjavaurldownloader.viewmodel;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.harlie.rxjavaurldownloader.MainActivity;
import com.harlie.rxjavaurldownloader.R;
import com.harlie.rxjavaurldownloader.model.AlbumUrl;

import java.util.ArrayList;
import java.util.List;


public class MainActivityPresenter {
    static final String TAG = "LEE: " + MainActivityPresenter.class.getSimpleName();

    Context context;
    AlbumUrlAdapter adapter;
    AlbumUrlViewHolder albumUrlViewHolder;

    public MainActivityPresenter(Context context, AlbumUrlAdapter adapter, AlbumUrlViewHolder albumUrlViewHolder) {
        this.context = context;
        this.adapter = adapter;
        this.albumUrlViewHolder = albumUrlViewHolder;
    }

    public MainActivityPresenter(Context context, AlbumUrlAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        this.albumUrlViewHolder = null;
    }

    public void doClick(AlbumUrl albumUrl) {
        Log.d(TAG, "-click-" + albumUrl);
        if (albumUrl.getAlbumUrlStatus() == AlbumUrl.AlbumUrlStatus.UNSELECTED) {
            albumUrl.setAlbumUrlStatus(AlbumUrl.AlbumUrlStatus.SELECTED);
            //FIXME: for better performance, maintain a list of selected objects
            int position = albumUrlViewHolder.getAdapterPosition();
            adapter.notifyItemChanged(position);
        }
        else {
            //make a beep sound
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
        }
    }

    public void createJob(View v) {
        Log.d(TAG, "createJob");
        List<String> selectedUrls = new ArrayList<>();
        boolean foundSelectedUrl = false;
        for (AlbumUrl albumUrl : adapter.getAlbumUrlList()) {
            if (albumUrl.getAlbumUrlStatus() == AlbumUrl.AlbumUrlStatus.SELECTED) {
                Log.d(TAG, "createJob: adding " + albumUrl.getAlbumPhotoUrl());
                selectedUrls.add(albumUrl.getAlbumPhotoUrl());
                foundSelectedUrl = true;
                albumUrl.setAlbumUrlStatus(AlbumUrl.AlbumUrlStatus.QUEUED);
            }
        }
        if (foundSelectedUrl) {
            Log.d(TAG, "queue the selectedUrls to URL Downloader Library");
            //FIXME: queue the selectedUrls to URL Downloader Library as a new Job
            Toast.makeText(context, R.string.job_created, Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(context, R.string.nothing_selected, Toast.LENGTH_SHORT).show();
        }
    }

    public void runAllJobs(View v) {
        Log.d(TAG, "runAllJobs");
        //FIXME: run list of Jobs queued in the URL Downloader Library
        Toast.makeText(context, R.string.no_jobs_queued, Toast.LENGTH_SHORT).show();
    }
}
