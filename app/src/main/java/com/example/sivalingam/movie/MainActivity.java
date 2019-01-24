package com.example.sivalingam.movie;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieList = new ArrayList<>();

        //Refer the recycler view from the layout
        mRecyclerView = findViewById(R.id.recycler_id);
        imageView = findViewById(R.id.error_int_id);
        textView = findViewById(R.id.error_message_id);
        layout = findViewById(R.id.refresh_id);

        //Grid layout manager
        GridLayoutManager manager = new GridLayoutManager(this, 2, 1, false);

        //Setting the layout manager for the recycler view
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);

        //Check if there is internet connectivity
        if(isOnline()){
            //We hav internet connection
            imageView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            fetchData(true);
        }
        else{
            //We dont dave internet connection
            mRecyclerView.setVisibility(View.INVISIBLE);
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
                    fetchData(true);
                } else {
                    layout.setRefreshing(false);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
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
            //Set the Retrofit instance
            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

            //This gets information from the API
            retrofit2.Call<OuterClass> call = service.getPopularMovies();
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

        //If testCondition is false fetch the top rated movies
        else {
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
                fetchData(true);
                return true;

            case R.id.action_rating:
                item.setChecked(true);
                fetchData(false);
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @return
     *
     * This function returns true if internet is available or false if internet is not available
     */
    public boolean isOnline() {
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

