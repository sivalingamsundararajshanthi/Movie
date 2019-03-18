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

    private Movie movie;

    private TrailerAdapter mTrailerAdapter;

    TextView year, rating, plot;
    ImageView imagePoster;
    Button mFavButton, mReviewButton;
    RecyclerView mRecyclerView;

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

        videoList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mTrailerAdapter = new TrailerAdapter(this);

        mAppdatabase = AppDatabase.getsInstance(getApplicationContext());

        //Enabling the action bar and the back button
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(BUNDLE_STRING)){
                try{
                    movie = savedInstanceState.getParcelable(BUNDLE_STRING);
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

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(mAppdatabase.movieDAO().loadMovieById(movie.getId()) != null){
                            movie.setFav(true);
                        }

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
    }

    private void addOrDeleteMovie(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(movie.isFav()){
                    movie.setFav(false);
                    mAppdatabase.movieDAO().deleteTask(movie);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFavButton.setText(getString(R.string.fav));
                        }
                    });
                } else {
                    movie.setFav(true);
                    mAppdatabase.movieDAO().insert(movie);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
        Video video = videoList.get(id);
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + video.getKey()));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }

    private void fetchData(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        retrofit2.Call<OuterVideo> call = service.getVideos(movie.getId());

        call.enqueue(new Callback<OuterVideo>() {
            @Override
            public void onResponse(Call<OuterVideo> call, Response<OuterVideo> response) {

                videoList.clear();
                videoList.addAll(response.body().getVideoList());
                mTrailerAdapter.setVideoList(response.body().getVideoList());
                mRecyclerView.setAdapter(mTrailerAdapter);
            }

            @Override
            public void onFailure(Call<OuterVideo> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_STRING, movie);
    }

    private void populateUI(Movie movie){
        //Setting the fields with information from the object

        try{
            ActionBar bar = getSupportActionBar();
            bar.setTitle(movie.getTitle());
        } catch (NullPointerException npe){
            Log.d("NullPointerException", "NullPointerException");
        }

        String[] splitString = movie.getRelease_date().split("-");
        year.setText(splitString[0]);
        String ratingString = "/10";
        String builder = String.valueOf(movie.getVote_average()) + ratingString;
        rating.setText(builder);

        if(movie.getOverview().equals(""))
            plot.setText(R.string.plot);
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

        if(movie.isFav()){
            mFavButton.setText(getString(R.string.rem_fav));
        }

        if(isOnline()){
            fetchData();
        } else {
            showSnackBar();
        }
    }

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
