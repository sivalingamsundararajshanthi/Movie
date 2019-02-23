package com.example.sivalingam.movie;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    //Variables
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private List<Movie> movieList;
    private ImageView imageView;
    private TextView textView;
    private SwipeRefreshLayout layout;
    private boolean testForSort;

    //AppDatabase variable
    private AppDatabase mAppDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testForSort = true;

        movieList = new ArrayList<>();

        //Refer the recycler view from the layout
        mRecyclerView = findViewById(R.id.recycler_id);
        imageView = findViewById(R.id.error_int_id);
        textView = findViewById(R.id.error_message_id);
        layout = findViewById(R.id.refresh_id);

        //Initialize the AppDatabase instance
        mAppDatabase = AppDatabase.getsInstance(getApplicationContext());

        //Grid layout manager
        GridLayoutManager manager = new GridLayoutManager(this, 2, 1, false);

        //Setting the layout manager for the recycler view
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);

        //Check if there is internet connectivity
        if(isOnline()){
            //We have internet connection
            imageView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            fetchData(true);
        }
        else{
            //We dont dave internet connection
            mRecyclerView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }

        //SwipeRefreshListener which again checks if internet is available or not
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isOnline()){
                    layout.setRefreshing(false);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    fetchData(testForSort);
                } else {
                    layout.setRefreshing(false);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });

        Log.d("LIFECYCLECALLBACK", "ONCREATE");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("LIFECYCLECALLBACK", "ONSTART");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LIFECYCLECALLBACK", "ONRESUME");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("LIFECYCLECALLBACK", "ONPAUSE");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("LIFECYCLECALLBACK", "ONSTOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("LIFECYCLECALLBACK", "ONDESTROY");
    }

    /**
     *
     * @param id
     *
     * This overriden method is used to transition to the new activity when a movie is clicked.
     */
    @Override
    public void onClick(int id) {
        Intent intent = new Intent(MainActivity.this, DetailView.class);
        intent.putExtra("MOVIEOBJECTPOSITION", movieList.get(id));
        startActivity(intent);
    }

    /**
     *
     * @param testCondition
     *
     * This function is used to fetch the data from the API using the Retrofit library.
     */
    private void fetchData(boolean testCondition){
        //If testCondition is true fetch popular movies
        if(testCondition){
            ActionBar bar = getSupportActionBar();
            bar.setTitle(R.string.popular);

            //Set the Retrofit instance
            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

            //This gets information from the API
            retrofit2.Call<OuterClass> call = service.getPopularMovies();
            call.enqueue(new Callback<OuterClass>() {
                @Override
                public void onResponse(Call<OuterClass> call, Response<OuterClass> response) {
                    //The API fetch was successful
                    try{
                        movieList = new ArrayList<>();
                        movieList = response.body().getMovieList();
                        mAdapter = new MovieAdapter(movieList, MainActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                    } catch(NullPointerException e){
                        Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OuterClass> call, Throwable t) {
                    //The API fetch was unsuccessful
                    Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //If testCondition is false fetch the top rated movies
        else if(!testCondition){
            ActionBar bar = getSupportActionBar();
            bar.setTitle(R.string.top);
            //Set the Retrofit instance
            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

            //This gets information from the API
            retrofit2.Call<OuterClass> call = service.getRatingMovies();
            call.enqueue(new Callback<OuterClass>() {
                @Override
                public void onResponse(Call<OuterClass> call, Response<OuterClass> response) {
                    //The API fetch was successful
                    movieList = new ArrayList<>();
                    movieList = response.body().getMovieList();
                    mAdapter = new MovieAdapter(movieList, MainActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onFailure(Call<OuterClass> call, Throwable t) {
                    //The API fetch was unsuccessful
                    Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     *
     * @param menu
     * @return
     *
     * This overriden function is used to inflate the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     *
     * This overriden function is used to handle the menu selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_popularity:
                item.setChecked(true);
                testForSort = true;
                if(isOnline()){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    fetchData(true);
                } else {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }

                return true;

            case R.id.action_rating:
                item.setChecked(true);
                testForSort = false;
                if(isOnline()){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    fetchData(false);
                } else {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
                return true;

            case R.id.fav_movies:
                item.setChecked(true);
                //movieList.clear();

                //fetch all favourite movies and display
                LiveData<List<Movie>> movies = mAppDatabase.movieDAO().getAllMovies();
                movies.observe(this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(@Nullable List<Movie> movies) {
                        Log.d("LIVEDATA", "CALLED");
                        movieList.clear();
                        movieList = movies;
                        mAdapter = new MovieAdapter(movieList, MainActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @return
     *
     * This function returns true if internet is available or false if internet is not available
     */
    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}

