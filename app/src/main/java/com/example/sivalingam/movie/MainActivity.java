package com.example.sivalingam.movie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //Variables
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Refer the recycler view from the layout
        mRecyclerView = findViewById(R.id.recycler_id);

        //Grid layout manager
        GridLayoutManager manager = new GridLayoutManager(this, 2, 1, false);

        //Setting the layout manager for the recycler view
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);

        fetchData(true);

        //Set the Retrofit instance and fetch the data
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        //This gets information from the API
        retrofit2.Call<OuterClass> call = service.getPopularMovies();
        call.enqueue(new Callback<OuterClass>() {
            @Override
            public void onResponse(Call<OuterClass> call, Response<OuterClass> response) {
                //The API fetch was successful
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Log.d("RESPONSE", String.valueOf(response.body().getMovieList().size()));

                mAdapter = new MovieAdapter(response.body().getMovieList());
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<OuterClass> call, Throwable t) {
                //The API fetch failed
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                    mAdapter = new MovieAdapter(movieList);
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
                    mAdapter = new MovieAdapter(movieList);
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
}

