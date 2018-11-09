package com.harlie.rxjavaurldownloader.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.harlie.rxjavaurldownloader.JobListActivity;
import com.harlie.rxjavaurldownloader.R;
import com.harlie.rxjavaurldownloader.model.AlbumUrl;
import com.harlie.urldownloaderlibrary.Job;
import com.harlie.urldownloaderlibrary.URLDownloader;

import java.util.ArrayList;
import java.util.List;


public class MainActivityPresenter {
    static final String TAG = "LEE: " + MainActivityPresenter.class.getSimpleName();

    public static int TIME_OUT = 30000;
    public static int RETRY_LIMIT = 4;

    private static int sCallback;

    private Context context;
    private AlbumUrlAdapter adapter;
    private AlbumUrlViewHolder albumUrlViewHolder;


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

    //FIXME: for better performance, maintain a list of selected objects
    public void doClick(AlbumUrl albumUrl) {
        Log.d(TAG, "-click-" + albumUrl);
        if (albumUrl.getAlbumUrlStatus() == AlbumUrl.AlbumUrlStatus.UNSELECTED) {
            albumUrl.setAlbumUrlStatus(AlbumUrl.AlbumUrlStatus.SELECTED);
            int position = albumUrlViewHolder.getAdapterPosition();
            adapter.notifyItemChanged(position);
        }
        else {
            if (albumUrl.getAlbumUrlStatus() == AlbumUrl.AlbumUrlStatus.SELECTED) {
                albumUrl.setAlbumUrlStatus(AlbumUrl.AlbumUrlStatus.UNSELECTED);
                int position = albumUrlViewHolder.getAdapterPosition();
                adapter.notifyItemChanged(position);
            }
            else {
                //make a beep sound
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
            }
        }
    }

    public void createJob(View v) {
        Log.d(TAG, "createJob");
        List<AlbumUrl> selectedAlbumUrls = new ArrayList<>();
        List<String> selectedUrls = new ArrayList<>();
        boolean foundSelectedUrl = false;
        for (AlbumUrl albumUrl : adapter.getAlbumUrlList()) {
            if (albumUrl.getAlbumUrlStatus() == AlbumUrl.AlbumUrlStatus.SELECTED) {
                Log.d(TAG, "createJob: adding " + albumUrl.getAlbumPhotoUrl());
                selectedAlbumUrls.add(albumUrl);
                selectedUrls.add(albumUrl.getAlbumPhotoUrl());
                foundSelectedUrl = true;
                albumUrl.setAlbumUrlStatus(AlbumUrl.AlbumUrlStatus.QUEUED);
            }
        }
        if (foundSelectedUrl) {
            Log.d(TAG, "queue the selectedUrls to URL Downloader Library");
            String callbackKey = String.valueOf(++sCallback);
            Job job = URLDownloader.createJob(selectedUrls, TIME_OUT, RETRY_LIMIT, callbackKey);
            for (AlbumUrl albumUrl : selectedAlbumUrls) {
                albumUrl.setJobId(job.getJobId());
            }
            Toast.makeText(context, R.string.job_created, Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(context, R.string.nothing_selected, Toast.LENGTH_SHORT).show();
        }
    }

    public void runAllJobs(View v) {
        Log.d(TAG, "runAllJobs");
        if (URLDownloader.getAllJobs() == null || URLDownloader.getAllJobs().size() == 0) {
            Toast.makeText(context, R.string.no_jobs_queued, Toast.LENGTH_SHORT).show();
        }
        else {
            URLDownloader.startJobs();
            Toast.makeText(context, R.string.jobs_running, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, JobListActivity.class);
            context.startActivity(intent);
        }
    }
}
