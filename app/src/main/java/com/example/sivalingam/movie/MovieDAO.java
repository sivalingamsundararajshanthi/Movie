package com.example.sivalingam.movie;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getAllMovies();

    @Insert
    void insert(Movie movie);

    @Delete
    void deleteTask(Movie movie);
}
