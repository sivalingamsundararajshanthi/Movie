package com.example.sivalingam.movie;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String POPULAR_SELECTION = "POPULAR";
    private static final String HIGHEST_SELECTION = "HIGHEST";
    private static final String FAVORITE_SELECTION = "FAVORITE";
    private static final String SAVED_SELECTION = "SELECTION";

    private static String selection;

    //Variables
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter, fAdapter;
    private List<Movie> movieList;
    private ImageView imageView;
    private TextView textView;
    private SwipeRefreshLayout layout;

//    private variable for
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up the view model for favorite movies
        setUpViewModel();

        //Initialize the movieList array list
        movieList = new ArrayList<>();

        //Initialize the app database instance
        appDatabase = AppDatabase.getsInstance(this);

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

        //Initialize the adapter
        mAdapter = new MovieAdapter(this);

        //Saved instance state in not null
        if(savedInstanceState != null){

            /*if(savedInstanceState.containsKey("MOVIE")){
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                movieList.clear();
                movieList = savedInstanceState.getParcelableArrayList("MOVIE");
                mRecyclerView.setAdapter(mAdapter);
            }*/


            if(savedInstanceState.containsKey(SAVED_SELECTION)){
                if(isOnline()){
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);

                    if(selection.equals(POPULAR_SELECTION) || selection.equals(HIGHEST_SELECTION)){
                        fetchData(selection);
                    } else {
                        layout.setEnabled(false);
                    }

                } else {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        }

        //Saved instance state is null
        else {

            //For the first time specify that the selection is popular
            selection = POPULAR_SELECTION;

            //Check if there is internet connectivity
            if(isOnline()){
                //We have internet connection
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

                fetchData(selection);
            }
            else{
                //We dont dave internet connection
                mRecyclerView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            }
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
                    fetchData(selection);
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
     * This function is used to initialize the view model so that the observer can listen for changes in the database
     * and update the UI.
     */
    private void setUpViewModel(){
        final MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {

                //resume is false, hence we need to show the favorite movies
                if(selection.equals(FAVORITE_SELECTION)){

                    //clear the movie list and add new data to the movie list
                    movieList.clear();
                    movieList.addAll(movies);

                    //set the adapter with new data from movie list
                    mAdapter.setMovieList(movies);

                    //set the adapter for recycler view
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_SELECTION, selection);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
     * @param selection
     *
     * This function is used to fetch the data from the API using the Retrofit library.
     */
    private void fetchData(String selection){

        //Initialize the service
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        retrofit2.Call<OuterClass> call;

        //If selection is popular
        if(selection.equals(POPULAR_SELECTION)){

            //set title of action bar as Popular
            ActionBar bar = getSupportActionBar();
            bar.setTitle(R.string.popular);
            call = service.getPopularMovies();
        }

        //else selection is highest rated
        else {

            //set title of action bar as highest rated
            ActionBar bar = getSupportActionBar();
            bar.setTitle(R.string.top);
            call = service.getRatingMovies();
        }

        call.enqueue(new Callback<OuterClass>() {
            @Override
            public void onResponse(Call<OuterClass> call, Response<OuterClass> response) {
                //The API fetch was successful
                try{

                    //Clear the movie list
                    movieList.clear();

                    //Copy movie list from response
                    movieList.addAll(response.body().getMovieList());

                    //give array list with new data to the adapter
                    mAdapter.setMovieList(movieList);

                    //set the adapter to the recycler view
                    mRecyclerView.setAdapter(mAdapter);
                } catch(NullPointerException e){
                    Log.d("NullPointerException", "NullPointerException");
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

        //if selection is not null
        if(selection != null){

            //Based on the value of selection the menu item will be selected
            switch (selection){
                case POPULAR_SELECTION:
                    MenuItem menuItem = menu.findItem(R.id.action_popularity);
                    menuItem.setChecked(true);
                    break;

                case HIGHEST_SELECTION:
                    MenuItem menuItem1 = menu.findItem(R.id.action_rating);
                    menuItem1.setChecked(true);
                    break;

                case FAVORITE_SELECTION:
                    MenuItem menuItem3 = menu.findItem(R.id.fav_movies);
                    menuItem3.setChecked(true);
                    break;
            }
        }

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
                layout.setEnabled(true);
                selection = POPULAR_SELECTION;

                if(isOnline()){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    fetchData(POPULAR_SELECTION);
                } else {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }

                return true;

            case R.id.action_rating:
                item.setChecked(true);
                layout.setEnabled(true);

                selection = HIGHEST_SELECTION;

                if(isOnline()){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    fetchData(HIGHEST_SELECTION);

                } else {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
                return true;

            case R.id.fav_movies:
                ActionBar bar = getSupportActionBar();
                bar.setTitle(R.string.fav_movies);
                item.setChecked(true);
                layout.setEnabled(false);

                selection = FAVORITE_SELECTION;

                setUpViewModel();
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

