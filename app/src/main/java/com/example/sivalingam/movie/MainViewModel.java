package com.example.sivalingam.movie;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

/**
 * This class is used to initialize the view model.
 */
public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;
    private Movie movie;

    public MainViewModel(@NonNull Application application) {
        super(application);

        Log.d("CHECKFOR", "here");

        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        movies = database.movieDAO().getAllMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
