package com.harlie.rxjavaurldownloader;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.harlie.rxjavaurldownloader.databinding.ActivityMainBinding;
import com.harlie.rxjavaurldownloader.model.AlbumUrl;
import com.harlie.rxjavaurldownloader.retrofit.AlbumUrlRetrofitClient;
import com.harlie.rxjavaurldownloader.retrofit.IMyAlbumUrlApi;
import com.harlie.rxjavaurldownloader.viewmodel.AlbumUrlAdapter;
import com.harlie.rxjavaurldownloader.viewmodel.MainActivityPresenter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static com.harlie.rxjavaurldownloader.retrofit.IMyAlbumUrlApi.ALBUM_WEBSERVER_URL;


public class MainActivity extends BaseActivity {
    static final String TAG = "LEE: " + MainActivity.class.getSimpleName();

    ActivityMainBinding binding;
    IMyAlbumUrlApi myAlbumUrlApi;
    RecyclerView albumUrlRecyclerView;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Retrofit retrofit = AlbumUrlRetrofitClient.getInstance(ALBUM_WEBSERVER_URL);
        myAlbumUrlApi = retrofit.create(IMyAlbumUrlApi.class);

        albumUrlRecyclerView = findViewById(R.id.album_url_recyclerview);
        albumUrlRecyclerView.setHasFixedSize(true);
        albumUrlRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadTheAlbumUrlList();
    }

    private void loadTheAlbumUrlList() {
        Log.d(TAG, "loadTheAlbumUrlList");
        findViewById(R.id.loading_panel).setVisibility(View.VISIBLE);
        compositeDisposable.add(myAlbumUrlApi.getListOfAlbumPhotoUrl()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<AlbumUrl>>() {
                @Override
                public void accept(List<AlbumUrl> albumUrls) throws Exception {
                    Log.d(TAG, "loadTheAlbumUrlList Consumer accept: albumUrls.size=" + albumUrls.size());;
                    displayAlbumUrlList(albumUrls);
                    findViewById(R.id.loading_panel).setVisibility(View.GONE);
                }
            }));
    }

    private void displayAlbumUrlList(List<AlbumUrl> albumUrls) {
        Log.d(TAG, "displayAlbumUrlList");
        AlbumUrlAdapter albumUrlAdapter = new AlbumUrlAdapter(this, albumUrls);
        albumUrlRecyclerView.setAdapter(albumUrlAdapter);
        MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this, albumUrlAdapter);
        binding.setPresenter(mainActivityPresenter);
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
