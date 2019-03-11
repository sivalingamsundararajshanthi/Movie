package com.example.sivalingam.movie;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * This class makes a call to the FetchData class and calls fetchData method inside that class and gets a mutable live
 * data.
 */
public class AddMovieViewModel extends ViewModel {

    private LiveData<Movie> movie;

    public AddMovieViewModel(AppDatabase mDatabase, int id){
        //movie = mDatabase.movieDAO().loadMovieById(id);
    }

    public LiveData<Movie> getMovie(){
        return movie;
    }
}
