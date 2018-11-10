package com.harlie.rxjavaurldownloader.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.harlie.rxjavaurldownloader.BaseActivity;
import com.harlie.rxjavaurldownloader.JobListActivity;
import com.harlie.rxjavaurldownloader.R;
import com.harlie.rxjavaurldownloader.model.AlbumUrl;
import com.harlie.urldownloaderlibrary.Job;
import com.harlie.urldownloaderlibrary.URLDownloader;

import java.util.ArrayList;
import java.util.List;

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
    public void doClick(View v, AlbumUrl albumUrl) {
        Log.d(TAG, "-click-" + albumUrl);
        if (albumUrl.getAlbumUrlStatus() == UNSELECTED) {
            albumUrl.setAlbumUrlStatus(SELECTED);
        }
        else {
            if (albumUrl.getAlbumUrlStatus() == SELECTED) {
                albumUrl.setAlbumUrlStatus(UNSELECTED);
            }
            else {
                //make a beep sound
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                return;
            }
        }
        updateSelectButton();
        int position = albumUrlViewHolder.getAdapterPosition();
        adapter.notifyItemChanged(position);
    }

    private void updateSelectButton() {
        if (context instanceof BaseActivity) {
            Log.d(TAG, "updateSelectButton"); //adjust the 'select' button
            BaseActivity baseActivity = (BaseActivity) context;
            Button selectButton = baseActivity.findViewById(R.id.select_all_button);
            int howManyCanBeSelected = howManyCanBeSelected();
            int howManySelected = howManySelected();
            if (howManyCanBeSelected == 0 && howManySelected == 0) {
                Log.d(TAG, "updateSelectButton: nothing can be selected!");
                selectButton.setVisibility(View.GONE);
                Button createJobButton = baseActivity.findViewById(R.id.create_job_button);
                createJobButton.setVisibility(View.GONE);
            }
            else {
                Log.d(TAG, "updateSelectButton: howManySelected=" + howManySelected + ", howManyCanBeSelected=" + howManyCanBeSelected);
                boolean haveSelections = howManySelected > 0;
                Resources resources = baseActivity.getResources();
                selectButton.setText(haveSelections
                        ? resources.getString(R.string.unselect_jobs)
                        : resources.getString(R.string.select_jobs));
                selectButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private int howManyCanBeSelected() {
        int count = 0;
        for (AlbumUrl albumUrl : adapter.getAlbumUrlList()) {
            if (albumUrl.getAlbumUrlStatus() == UNSELECTED) {
                ++count;
            }
        }
        return count;
    }

    private int howManySelected() {
        int count = 0;
        for (AlbumUrl albumUrl : adapter.getAlbumUrlList()) {
            if (albumUrl.getAlbumUrlStatus() == SELECTED) {
                ++count;
            }
        }
        return count;
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
            int callbackKey = ++sCallback;
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
        updateSelectButton();
    }

    public void manageJobList(View v) {
        Log.d(TAG, "manageJobList");
        if (URLDownloader.getAllJobs() == null || URLDownloader.getAllJobs().size() == 0) {
            Toast.makeText(context, R.string.no_jobs_queued, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, R.string.manage_jobs, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, JobListActivity.class);
            context.startActivity(intent);
        }
    }

    public void selectAllAlbums(View v) {
        Log.d(TAG, "selectAllAlbums");
        boolean selectedSomething = false;
        boolean unselectedSomething = false;
        boolean haveSelections = howManySelected() > 0;
        for (AlbumUrl albumUrl : adapter.getAlbumUrlList()) {
            if (haveSelections) { // we unselect everything selected in this case
                if (albumUrl.getAlbumUrlStatus() == SELECTED) {
                    Log.d(TAG, "selectAllAlbums: unselecting " + albumUrl.getAlbumPhotoUrl());
                    albumUrl.setAlbumUrlStatus(UNSELECTED);
                    unselectedSomething = true;
                }
            }
            else { // select everything unselected
                if (albumUrl.getAlbumUrlStatus() == UNSELECTED) {
                    Log.d(TAG, "selectAllAlbums: selecting " + albumUrl.getAlbumPhotoUrl());
                    albumUrl.setAlbumUrlStatus(SELECTED);
                    selectedSomething = true;
                }
            }
        }
        if (unselectedSomething) {
            Toast.makeText(context, R.string.unselected, Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            Button button = (Button) v;
            button.setText(v.getResources().getString(R.string.select_jobs));
            updateSelectButton();
        }
        else if (selectedSomething) {
            Toast.makeText(context, R.string.everything_selected, Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            Button button = (Button) v;
            button.setText(v.getResources().getString(R.string.unselect_jobs));
            updateSelectButton();
        }
        else {
            Toast.makeText(context, R.string.nothing_selected, Toast.LENGTH_SHORT).show();
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
            case CANCELLED:
                color = resources.getColor(R.color.color_CANCELLED);
                break;
        }
        return color;
    }

    public Context getContext() {
        return context;
    }
}
