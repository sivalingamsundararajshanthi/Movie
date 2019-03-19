package com.example.sivalingam.movie;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * This class is not used
 */
public class AddMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private AppDatabase mAppDatabase;
    private int id;

    public AddMovieViewModelFactory(AppDatabase mAppDatabase, int id){
        this.mAppDatabase = mAppDatabase;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddMovieViewModel(mAppDatabase, id);
    }
}
