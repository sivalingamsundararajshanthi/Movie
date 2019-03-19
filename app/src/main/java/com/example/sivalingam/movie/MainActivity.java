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
    private MovieAdapter mAdapter;
    private List<Movie> movieList;
    private ImageView imageView;
    private TextView textView;
    private SwipeRefreshLayout layout;

//  private variable for
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

            //saved instance state contains a key called SAVED_SELECTION
            if(savedInstanceState.containsKey(SAVED_SELECTION)){

                //user selection is popular or highest rated movies
                if(selection.equals(POPULAR_SELECTION) || selection.equals(HIGHEST_SELECTION)){

                    //user is online
                    if(isOnline()){

                        //set visibility of error image and textview to invisible
                        imageView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);

                        //Set visibility of recycler view to visible
                        mRecyclerView.setVisibility(View.VISIBLE);

                        //Fetch data from the internet
                        fetchData(selection);
                    }

                    //user is offline
                    else {

                        //Set recycler view to visibility to visible
                        mRecyclerView.setVisibility(View.INVISIBLE);

                        //Set visibility of error image and textview to invisible
                        imageView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }
                }

                //user selection is favorite movies
                else {

                    //disable the swipe refresh layout
                    mRecyclerView.setVisibility(View.VISIBLE);

                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    layout.setEnabled(false);
                }

                //if user is online
//                if(isOnline()){
//
//                    //setting imageview and error text view are set to null
//                    imageView.setVisibility(View.INVISIBLE);
//                    textView.setVisibility(View.INVISIBLE);
//
//                    //if user selected popular or top rated movie
//                    if(selection.equals(POPULAR_SELECTION) || selection.equals(HIGHEST_SELECTION)){
//
//                        //fetch data from the internet
//                        fetchData(selection);
//                    }
//
//                    //if user selected favorite movies
//                    else {
//
//                        //disable the swipe refresh layout
//                        layout.setEnabled(false);
//                    }
//                }
//
//                //If user is not online
//                else {
//
//                    //hide the recycler view
//                    mRecyclerView.setVisibility(View.INVISIBLE);
//
//                    //Make image view and textview error visible
//                    imageView.setVisibility(View.VISIBLE);
//                    textView.setVisibility(View.VISIBLE);
//                }
            }
        }

        //Saved instance state is null
        else {

            //For the first time specify that the selection is popular
            selection = POPULAR_SELECTION;

            //If user is online
            if(isOnline()){

                //Hide image view and error textview
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

                //Fetch data from the internet
                fetchData(selection);
            }

            //User is offline
            else{

                //set recycler view to invisible
                mRecyclerView.setVisibility(View.INVISIBLE);

                //set image view and error textview as visible
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            }
        }

        //SwipeRefreshListener which again checks if internet is available or not
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //The user is online
                if(isOnline()){


                    layout.setRefreshing(false);

                    //set recycler view visibility to visible
                    mRecyclerView.setVisibility(View.VISIBLE);

                    //set error state to false
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);

                    //fetch data from internet
                    fetchData(selection);
                }

                //The user does not have an internet connection
                else {

                    layout.setRefreshing(false);

                    //Set recycler view visibility to invisible
                    mRecyclerView.setVisibility(View.INVISIBLE);

                    //Set error state to false
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     *
     * @param outState
     *
     * This overriden function is used to cache the user selection.
     */
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
            ActionBar bar = getSupportActionBar();
            //Based on the value of selection the menu item will be selected
            switch (selection){
                case POPULAR_SELECTION:

                    bar.setTitle(R.string.popular);
                    MenuItem menuItem = menu.findItem(R.id.action_popularity);
                    menuItem.setChecked(true);
                    break;

                case HIGHEST_SELECTION:
                    bar.setTitle(R.string.top);
                    MenuItem menuItem1 = menu.findItem(R.id.action_rating);
                    menuItem1.setChecked(true);
                    break;

                case FAVORITE_SELECTION:
                    bar.setTitle(R.string.fav_movies);
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

        ActionBar bar = getSupportActionBar();

        switch (id){
            case R.id.action_popularity:
                bar.setTitle(R.string.popular);
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
                bar.setTitle(R.string.top);
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
                bar.setTitle(R.string.fav_movies);
                item.setChecked(true);
                layout.setEnabled(false);

                mRecyclerView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

                selection = FAVORITE_SELECTION;

                setUpViewModel();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This function is used to initialize the view model so that the observer can listen for changes in the database
     * and update the UI.
     */
    private void setUpViewModel(){
        Log.d("CHECKFOR", "Method");

        final MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {

                Log.d("CHECKFOR", "Reciving database update from live data");

                //Selection is FAVORITE_SELECTION therefore clear the array list and load new data from live data and
                //show it in recycler view
                if(selection.equals(FAVORITE_SELECTION)){

                    Log.d("CHECKFOR", "inside if");

                    //clear the movie list and add new data to the movie list
                    movieList.clear();
                    movieList.addAll(movies);

                    Log.d("CHECKFOR", "" + movies.size());

                    //set the adapter with new data from movie list
                    mAdapter.setMovieList(movies);

                    //set the adapter for recycler view
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
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

