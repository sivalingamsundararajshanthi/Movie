package com.example.sivalingam.movie;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        //variables
        TextView name = findViewById(R.id.moview_name_id);
        TextView year = findViewById(R.id.year_id);
        TextView rating = findViewById(R.id.rating_id);
        TextView plot = findViewById(R.id.synopsis_id);
        ImageView imagePoster = findViewById(R.id.movie_poster_id);

        //Enabling the action bar and the back button
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting the intent
        Intent intent = getIntent();

        //If the intent is not null
        if (intent != null) {

            //get the movie object from the main activity
            Movie movie = intent.getParcelableExtra("MOVIEOBJECTPOSITION");

            try {
                //Setting the fields with information from the object
                name.setText(movie.getTitle());
                String[] splitString = movie.getRelease_date().split("-");
                year.setText(splitString[0]);
                Resources resources = getResources();
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


            } catch (NullPointerException e) {
                Log.d("ERROR", e.getLocalizedMessage());
            }
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
