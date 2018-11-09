package com.harlie.rxjavaurldownloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.harlie.rxjavaurldownloader.model.AlbumUrl;
import com.harlie.rxjavaurldownloader.retrofit.IMyAlbumUrlApi;
import com.harlie.rxjavaurldownloader.retrofit.AlbumUrlRetrofitClient;
import com.harlie.rxjavaurldownloader.viewholder.AlbumUrlAdapter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {
    static final String TAG = "LEE: " + MainActivity.class.getSimpleName();

    IMyAlbumUrlApi myAlbumUrlApi;
    RecyclerView albumUrlRecyclerView;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = AlbumUrlRetrofitClient.getInstance();
        myAlbumUrlApi = retrofit.create(IMyAlbumUrlApi.class);

        albumUrlRecyclerView = findViewById(R.id.album_url_recyclerview);
        albumUrlRecyclerView.setHasFixedSize(true);
        albumUrlRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadTheAlbumUrlJobList();
    }

    private void loadTheAlbumUrlJobList() {
        Log.d(TAG, "loadTheAlbumUrlJobList");
        compositeDisposable.add(myAlbumUrlApi.getAlbumPhotos()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<AlbumUrl>>() {
                @Override
                public void accept(List<AlbumUrl> albumUrls) throws Exception {
                    Log.d(TAG, "loadTheAlbumUrlJobList Consumer accept: albumUrls.size=" + albumUrls.size());;
                    displayAlbumUrlList(albumUrls);
                }
            }));
    }

    private void displayAlbumUrlList(List<AlbumUrl> albumUrls) {
        Log.d(TAG, "displayAlbumUrlList");
        AlbumUrlAdapter albumUrlAdapter = new AlbumUrlAdapter(this, albumUrls);
        albumUrlRecyclerView.setAdapter(albumUrlAdapter);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
