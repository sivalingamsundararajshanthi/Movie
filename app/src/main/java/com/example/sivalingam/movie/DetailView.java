package com.example.sivalingam.movie;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DetailView extends AppCompatActivity {

    private final String BUNDLE_STRING = "MOVIEOBJECT";
    private final String BUNDLE_BOOLEAN = "BOOLEANDB";

    //Member variable for the database
    private AppDatabase mAppdatabase;

    private Movie movie;

    private boolean inDb;

    TextView name, year, rating, plot;
    ImageView imagePoster;
    Button mFavButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        //variables
        name = findViewById(R.id.moview_name_id);
        year = findViewById(R.id.year_id);
        rating = findViewById(R.id.rating_id);
        plot = findViewById(R.id.synopsis_id);
        imagePoster = findViewById(R.id.movie_poster_id);
        mFavButton = findViewById(R.id.fav_btn_id);

        mAppdatabase = AppDatabase.getsInstance(getApplicationContext());

        //Enabling the action bar and the back button
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null){

            Log.d("bothtrue", "saved");

            if(savedInstanceState.containsKey(BUNDLE_STRING)){

                Log.d("bothtrue", "bothtrue");

                try{
                    movie = savedInstanceState.getParcelable(BUNDLE_STRING);
                    populateUI(movie,
                            savedInstanceState.getBoolean(BUNDLE_BOOLEAN));

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

                if(mAppdatabase.movieDAO().loadMovieById(movie.getId()) != null){
                    inDb = true;
                    movie.setFav(true);
                }

                try {
                    populateUI(movie, inDb);
                } catch (NullPointerException e) {
                    Log.d("ERROR", e.getLocalizedMessage());
                }
            }
        }

        //Onclicklistener for "Make Favourite" button or "Remove Favourite" Button
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If movie object is not null
                if(movie != null){

                    //If movie is not favourite
                    if(mAppdatabase.movieDAO().loadMovieById(movie.getId()) == null){
                        movie.setFav(true);

                        try{
                            mAppdatabase.movieDAO().insert(movie);
                            finish();
                        } catch(SQLiteConstraintException sql){
                            Toast.makeText(DetailView.this, "Movie already in favourites", Toast.LENGTH_SHORT).show();
                        }
                    }

                    //If movie is favourite
                    else {

                        //Delete the movie from database
                        mAppdatabase.movieDAO().deleteTask(movie);
                        finish();
                    }
                } else {
                    Toast.makeText(DetailView.this, "Error in movie object", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_STRING, movie);
        outState.putBoolean(BUNDLE_BOOLEAN, inDb);
    }

    private void populateUI(Movie movie, boolean test){
        //Setting the fields with information from the object
        name.setText(movie.getTitle());
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
