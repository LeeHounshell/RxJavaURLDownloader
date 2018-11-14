package com.harlie.rxjavaurldownloader.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.harlie.rxjavaurldownloader.R;
import com.harlie.urldownloaderlibrary.URLDownloader;

import org.greenrobot.eventbus.EventBus;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UrlDownloaderRequestDownloadService extends IntentService {
    static final String TAG = "LEE: " + UrlDownloaderRequestDownloadService.class.getSimpleName();

    private static final String ACTION_START_JOBS = "com.harlie.rxjavaurldownloader.service.action.START_JOBS";
    private static final String ACTION_PAUSE_JOBS = "com.harlie.rxjavaurldownloader.service.action.PAUSE_JOBS";
    private static final String ACTION_STOP_JOBS = "com.harlie.rxjavaurldownloader.service.action.STOP_JOBS";

    public UrlDownloaderRequestDownloadService() {
        super("UrlDownloaderRequestDownloadService");
    }

    /**
     * Starts this service to perform action StartJobs.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionStartJobs(Context context) {
        Intent intent = new Intent(context, UrlDownloaderRequestDownloadService.class);
        intent.setAction(ACTION_START_JOBS);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action PauseJobs.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionPauseJobs(Context context) {
        Intent intent = new Intent(context, UrlDownloaderRequestDownloadService.class);
        intent.setAction(ACTION_PAUSE_JOBS);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action StopJobs.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionStopJobs(Context context) {
        Intent intent = new Intent(context, UrlDownloaderRequestDownloadService.class);
        intent.setAction(ACTION_STOP_JOBS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_JOBS.equals(action)) {
                Log.d(TAG, "onHandleIntent: ACTION_START_JOBS");
                handleActionStartJobs();
            } else if (ACTION_PAUSE_JOBS.equals(action)) {
                Log.d(TAG, "onHandleIntent: ACTION_PAUSE_JOBS");
                handleActionPauseJobs();
            } else if (ACTION_STOP_JOBS.equals(action)) {
                Log.d(TAG, "onHandleIntent: ACTION_STOP_JOBS");
                handleActionStopJobs();
            }
        }
    }

    /**
     * Handle action StartJobs in the provided background thread.
     */
    private void handleActionStartJobs() {
        Log.d(TAG, "handleActionStartJobs");
        if (URLDownloader.getInstance().getAllJobs() == null || URLDownloader.getInstance().getAllJobs().size() == 0) {
            Toast.makeText(this, R.string.no_jobs_queued, Toast.LENGTH_SHORT).show();
        }
        else {
            URLDownloader.getInstance().startJobs();
            Toast.makeText(this, R.string.jobs_running, Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }
    }

    /**
     * Handle action PauseJobs in the provided background thread.
     */
    private void handleActionPauseJobs() {
        Log.d(TAG, "handleActionPauseJobs");
        if (URLDownloader.getInstance().getAllJobs() == null || URLDownloader.getInstance().getAllJobs().size() == 0) {
            Toast.makeText(this, R.string.no_jobs_queued, Toast.LENGTH_SHORT).show();
        }
        else {
            URLDownloader.getInstance().pauseJobs();
            Toast.makeText(this, R.string.jobs_paused, Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }
    }

    /**
     * Handle action StopJobs in the provided background thread.
     */
    private void handleActionStopJobs() {
        Log.d(TAG, "handleActionStopJobs");
        if (URLDownloader.getInstance().getAllJobs() == null || URLDownloader.getInstance().getAllJobs().size() == 0) {
            Toast.makeText(this, R.string.no_jobs_queued, Toast.LENGTH_SHORT).show();
        }
        else {
            URLDownloader.getInstance().stopJobs();
            Toast.makeText(this, R.string.jobs_stopped, Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }
    }

    public class NotifyDataSetEvent {
        // empty event
    }

    private void notifyDataSetChanged() {
        Log.d(TAG, "notifyDataSetChanged");
        NotifyDataSetEvent notifyDataSetEvent = new NotifyDataSetEvent();
        EventBus.getDefault().post(notifyDataSetEvent); // implement notify using EventBus
    }

}
