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
Items can only be placed into a single job queue and can not be reselected.
After all desired job sets are created, press "Run All Jobs" to start jobs and
transition to view the jobs list.  Jobs are managed from the jobs list Activity.


URL Downloader Library
======================

## Description

File downloading and the job work queue are genericly handled by the AAR: URLDownloaderLibrary
This is the "heavy-lifter" responsible for doing all the real work of managing the downloads.
When a job is run, all URLs in that job download concurrently. Multiple jobs run concurrently.
Jobs invoke a callback on completion. The callback receives a map of urls and each url result.
There are no limits on the number of concurrent tasks.

The URLDownloader API:
 * createJob
 * getAllJobs
 * getUrlsForJob
 * startJobs
 * pauseJobs
 * stopJobs

The createJob Parameters:
 * a list of URLs to be downloaded
 * a timeout value in seconds
 * the number of retries
 * a callback function

The Job API:
 * constructor (create Job)
 * getUrlsForJob
 * start
 * pause
 * unpause
 * cancel

