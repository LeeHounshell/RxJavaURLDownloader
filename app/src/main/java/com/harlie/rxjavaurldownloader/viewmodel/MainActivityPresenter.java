package com.harlie.rxjavaurldownloader.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import static com.harlie.rxjavaurldownloader.model.AlbumUrl.AlbumUrlStatus.COMPLETE;
import static com.harlie.rxjavaurldownloader.model.AlbumUrl.AlbumUrlStatus.DOWNLOADING;
import static com.harlie.rxjavaurldownloader.model.AlbumUrl.AlbumUrlStatus.QUEUED;
import static com.harlie.rxjavaurldownloader.model.AlbumUrl.AlbumUrlStatus.SELECTED;
import static com.harlie.rxjavaurldownloader.model.AlbumUrl.AlbumUrlStatus.UNSELECTED;


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
        if (albumUrl.getAlbumUrlStatus() == UNSELECTED) {
            albumUrl.setAlbumUrlStatus(SELECTED);
            int position = albumUrlViewHolder.getAdapterPosition();
            adapter.notifyItemChanged(position);
        }
        else {
            if (albumUrl.getAlbumUrlStatus() == SELECTED) {
                albumUrl.setAlbumUrlStatus(UNSELECTED);
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
            if (albumUrl.getAlbumUrlStatus() == SELECTED) {
                Log.d(TAG, "createJob: adding " + albumUrl.getAlbumPhotoUrl());
                selectedAlbumUrls.add(albumUrl);
                selectedUrls.add(albumUrl.getAlbumPhotoUrl());
                foundSelectedUrl = true;
                albumUrl.setAlbumUrlStatus(QUEUED);
            }
        }
        if (foundSelectedUrl) {
            Log.d(TAG, "queue the selectedUrls to URL Downloader Library");
            String callbackKey = String.valueOf(++sCallback);
            Job job = URLDownloader.createJob(selectedUrls, TIME_OUT, RETRY_LIMIT, callbackKey);
            if (job == null) {
                Toast.makeText(context, R.string.job_failed, Toast.LENGTH_SHORT).show();
                return;
            }
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

    public int getBackgroundColor(AlbumUrl albumUrl) {
        Resources resources = context.getResources();
        int color = 0;
        switch (albumUrl.getAlbumUrlStatus()) {
            case UNSELECTED:
                color = resources.getColor(R.color.color_UNSELECTED);
                break;
            case SELECTED:
                color = resources.getColor(R.color.color_SELECTED);
                break;
            case QUEUED:
                color = resources.getColor(R.color.color_QUEUED);
                break;
            case DOWNLOADING:
                color = resources.getColor(R.color.color_DOWNLOADING);
                break;
            case COMPLETE:
                color = resources.getColor(R.color.color_COMPLETE);
                break;
        }
        return color;
    }
}
