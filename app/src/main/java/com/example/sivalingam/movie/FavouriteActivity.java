package com.example.sivalingam.movie;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is not used
 */
public class FavouriteActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    //private variables
    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private AppDatabase appDatabase;
    private List<Movie> movieList;
    private static final String MESSAGE = "TYPE";
    private static final String POPULARITY = "POPULARITY";
    private static final String RATED = "RATED";
    private static final int result = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        movieList = new ArrayList<>();

        //Setting the recycler view
        mRecyclerView = findViewById(R.id.fav_rec_id);

        //Getting instance of appdatabase
        appDatabase = AppDatabase.getsInstance(this);

        //Grid layout manager
        GridLayoutManager manager = new GridLayoutManager(this, 2, 1, false);

        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);

        populateData();
    }

    private void populateData(){
        final MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                movieList.clear();
                movieList = movies;
                movieAdapter = new MovieAdapter(FavouriteActivity.this);
                mRecyclerView.setAdapter(movieAdapter);
            }
        });
    }

    @Override
    public void onClick(int id) {
        Intent intent = new Intent(FavouriteActivity.this, DetailView.class);
        intent.putExtra("MOVIEOBJECTPOSITION", movieList.get(id));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_popularity:
                Intent intent = new Intent();
                intent.putExtra(MESSAGE, POPULARITY);
                setResult(result, intent);
                finish();

            case R.id.action_rating:
                 Intent intent1 = new Intent();
                 intent1.putExtra(MESSAGE, RATED);
                 setResult(result, intent1);
                 finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
