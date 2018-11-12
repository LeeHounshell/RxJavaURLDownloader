RxJava URL Downloader App
=========================

## Description

Coding challenge for a RxJava and Retrofit URL Downloader.  Project uses the Fake JSON Server at https://jsonplaceholder.typicode.com to dummy up a list of URL download jobs.

When the app is first run, it uses RxJava and Retrofit to process the JSON at:
<b>https://jsonplaceholder.typicode.com/photos</b>  Each of the JSON entries
represents album data and contains URLs for the album's cover photo and thumnail.
That data is presented in a RecyclerView using Data Binding.

Click on one or more items to "select" them for the next job. They will change color.
Press the "Create a Job" button to add the set of selected items into a new job queue.
Items can only be placed into a single job queue and can not be reselected for another.
After all desired job sets are created, press "Manage Jobs" to manage to the jobs queue.


URL Downloader Library
======================

## Description

File downloading and the job work queue are generically handled by the AAR: URLDownloaderLibrary This is the "heavy-lifter" responsible for doing all the real work of managing the downloads.
When a job is run, all URLs in that job download using RxJava and Retrofit.
Multiple jobs run concurrently.  Jobs emit a callback event on completion via the Event Bus.
The callback includes a map of urls and each url result. See http://greenrobot.org/eventbus/
Library users that want to intercept the callback need to Subscribe to the event like this:

```
    @Subscribe
    public void onEvent(Job.URLDownloaderJobCompletionEvent completionEvent) {
        // code to handle the event
    }
```

This completionEvent holds:
 * a map of urls and url results
 * file names used for each url
 * the SHA-1 hash for each file
 * the initial job request


The URLDownloader API:
 * createJob
 * getJob
 * getAllJobs
 * getUrlsForJob
 * startJobs
 * pauseJobs
 * stopJobs

The createJob Parameters:
 * a list of URLs to be downloaded
 * a timeout value in seconds
 * the number of retries
 * a callback

The Job API:
 * constructor (create Job)
 * getUrlsForJob
 * start
 * pause
 * unpause
 * cancel


---
SCREENSHOTS
---


![screen](../master/screens/create_jobs.png)

![screen](../master/screens/job_list.png)

![screen](../master/screens/job_details.png)

