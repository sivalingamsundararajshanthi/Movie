package com.example.sivalingam.movie;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailView extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {

    private final String BUNDLE_STRING = "MOVIEOBJECT";

    //Member variable for the database
    private AppDatabase mAppdatabase;

    //Movie object
    private Movie movie;

    //This is the recycler view adapter for trailers
    private TrailerAdapter mTrailerAdapter;

    //Variables
    TextView year, rating, plot, errorTV;
    ImageView imagePoster;
    Button mFavButton, mReviewButton, errorButton;
    RecyclerView mRecyclerView;

    //List of videos
    private List<Video> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        //variables
        year = findViewById(R.id.year_id);
        rating = findViewById(R.id.rating_id);
        plot = findViewById(R.id.synopsis_id);
        imagePoster = findViewById(R.id.movie_poster_id);
        mFavButton = findViewById(R.id.fav_btn_id);
        mRecyclerView = findViewById(R.id.trailerRecyclerViewId);
        mReviewButton = findViewById(R.id.reviewBtnId);
        errorTV = findViewById(R.id.detail_error_tv_id);
        errorButton = findViewById(R.id.detail_error_btn_id);

        //Initialize the array list
        videoList = new ArrayList<>();

        //Linear layout manager to display the list of videos
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //Setting the layout manager for trailer recycler view
        mRecyclerView.setLayoutManager(layoutManager);

        //Setting the recycler view to a fixed size
        mRecyclerView.setHasFixedSize(true);

        //Setting the trailer recycler view
        mTrailerAdapter = new TrailerAdapter(this);

        //Getting an instance of app database
        mAppdatabase = AppDatabase.getsInstance(getApplicationContext());

        //Enabling the action bar and the back button
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //If saved instance is not null
        if(savedInstanceState != null){

            //If saved instance contains the key BUNDLE_STRING
            if(savedInstanceState.containsKey(BUNDLE_STRING)){
                try{

                    //Get the movie object from the saved instance state
                    movie = savedInstanceState.getParcelable(BUNDLE_STRING);

                    //Call to populateUI method passing in the movie object
                    populateUI(movie);

                } catch (NullPointerException npe) {
                    Log.d("ISNULL", "NULL");
                }
            }
        } else {

            //Getting the intent
            Intent intent = getIntent();

            //If the intent is not null
            if (intent != null) {

                //get the movie object from the main activity
                movie = intent.getParcelableExtra("MOVIEOBJECTPOSITION");

                //Here we use thread and runnable to find out if the movie object is present in the database or not
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        //if movie object is present in the database
                        if(mAppdatabase.movieDAO().loadMovieById(movie.getId()) != null){

                            //setting the isFavorite field of the movie object to true
                            movie.setFav(true);
                        }

                        //This thread can be used to update the UI.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    populateUI(movie);
                                } catch (NullPointerException e) {
                                    Log.d("ERROR", e.getLocalizedMessage());
                                }
                            }
                        });
                    }
                });
            }
        }

        //Onclicklistener for "Make Favourite" button or "Remove Favourite" Button
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If movie object is not null
                if(movie != null){
                    addOrDeleteMovie();
                } else {
                    Toast.makeText(DetailView.this, "Error in movie object", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //onClickListener for read reviews button
        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailView.this, ReviewActivity.class);

                //We pass the movie id
                intent.putExtra("MOVIEIDREVIEW", movie.getId());
                startActivity(intent);
            }
        });

        //OnClickListener for error button
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //User in online
                if(isOnline()){

                    //Set visibility of error button and error textview to invisible
                    errorButton.setVisibility(View.INVISIBLE);
                    errorTV.setVisibility(View.INVISIBLE);

                    //Set recycler view visibility to visible
                    mRecyclerView.setVisibility(View.VISIBLE);
                    fetchData();
                }

                //User is offline
                else {

                    //Toast message to indicate user is offline
                    Toast.makeText(DetailView.this, "No internet connection", Toast.LENGTH_SHORT).show();

                    //Set error textview and error button to visible
                    errorTV.setVisibility(View.VISIBLE);
                    errorButton.setVisibility(View.VISIBLE);

                    //Set recycler view to invisible
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * This function is used to either add or delete the movie in the room database
     */
    private void addOrDeleteMovie(){

        //Get instance of runnable
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                //If movie is favorite
                if(movie.isFav()){

                    //Set movie object's isFavorite field to false
                    movie.setFav(false);

                    //Delete the movie object from the database
                    mAppdatabase.movieDAO().deleteTask(movie);


                    //Update the UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //Set the buttons text to Make Favorite
                            mFavButton.setText(getString(R.string.fav));
                        }
                    });
                }

                //If movie is not favorite
                else {

                    //Set the isFavorite field of the movie object to true
                    movie.setFav(true);

                    //Insert the movie object into the database
                    mAppdatabase.movieDAO().insert(movie);

                    //Update the UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //Set the text of the button to remove favorite
                            mFavButton.setText(getString(R.string.rem_fav));
                        }
                    });
                }
            }
        });
    }

    /**
     *
     * @param id
     *
     * This overriden function is used to open the youtube or web browser.
     */
    @Override
    public void onClick(int id) {

        //Get the video object from the video list
        Video video = videoList.get(id);

        //Intent to open youtube
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));

        ///Intent to open web browser
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + video.getKey()));
        try {

            //Try to open youtube app
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            //If youtube app is not installed open in web browser
            this.startActivity(webIntent);
        }
    }

    /**
     * This function is used to fetch data from the internet
     */
    private void fetchData(){

        //Initialize the service
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        retrofit2.Call<OuterVideo> call = service.getVideos(movie.getId());

        call.enqueue(new Callback<OuterVideo>() {
            @Override
            public void onResponse(Call<OuterVideo> call, Response<OuterVideo> response) {

                //The API fetch was successful
                //Clear the video list
                videoList.clear();

                //Add content from the internet to the video list
                videoList.addAll(response.body().getVideoList());

                if(videoList.isEmpty()){
                    errorTV.setText(getString(R.string.no_video_available));
                    errorTV.setVisibility(View.VISIBLE);
                }

                //Pass video list to the recycler view
                mTrailerAdapter.setVideoList(response.body().getVideoList());

                //Set the adapter of recycler view
                mRecyclerView.setAdapter(mTrailerAdapter);
            }

            @Override
            public void onFailure(Call<OuterVideo> call, Throwable t) {

            }
        });
    }

    /**
     *
     * @param outState
     *
     * This overriden function is used to cache the movie object in the saved instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Caching the movie object in the saved instance state
        outState.putParcelable(BUNDLE_STRING, movie);
    }

    /**
     *
     * @param movie
     *
     * This function is used to populate the UI with data from the movie object
     */
    private void populateUI(Movie movie){

        try{

            //Setting the title of the action bar
            ActionBar bar = getSupportActionBar();
            bar.setTitle(movie.getTitle());
        } catch (NullPointerException npe){
            Log.d("NullPointerException", "NullPointerException");
        }

        //Split the release data string
        String[] splitString = movie.getRelease_date().split("-");

        //Get the year from the splitString and set it
        year.setText(splitString[0]);

        //Build the rating string and set it
        String ratingString = "/10";
        String builder = String.valueOf(movie.getVote_average()) + ratingString;
        rating.setText(builder);

        //If movie plot is not available set it to Plot not available
        if(movie.getOverview().equals(""))
            plot.setText(R.string.plot);

        //If the plot is available set it to the plot
        else
            plot.setText(movie.getOverview());

        //URL for fetching the image
        String url = "http://image.tmdb.org/t/p/w185";

        //Parse the URL into an URI with parameters
        Uri uri = Uri.parse(url).buildUpon()
                .appendEncodedPath(movie.getPoster_path())
                .build();

        //Use Picasso to fetch the image
        Picasso.get().load(uri).placeholder(R.drawable.ic_action_movie_error)
                .error(R.drawable.ic_action_movie_error).into(imagePoster);

        //If the movie is favorite set text of button to remove favorite
        if(movie.isFav()){
            mFavButton.setText(getString(R.string.rem_fav));
        }

        //If user is online fetch data from internet and display it in the recycler view
        if(isOnline()){
            errorTV.setVisibility(View.INVISIBLE);
            errorButton.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            fetchData();
        }

        //If user is not online show snack bar with error
        else {
            errorTV.setVisibility(View.VISIBLE);
            errorButton.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * This function is not used.
     */
    private void showSnackBar(){
        View view = findViewById(R.id.reviewButtonId);

        String str = "error";

        final Snackbar snackbar = Snackbar.make(view, str, Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Try again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    fetchData();
                } else {
                    showSnackBar();
                }
            }
        });

        snackbar.show();
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

    /**
     *
     * @param item
     * @return
     *
     * This overriden function is used to enable the navigation to the previous activity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
