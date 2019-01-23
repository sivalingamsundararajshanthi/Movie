package com.example.sivalingam.movie;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailView extends AppCompatActivity {

    private TextView name, year, time, rating, plot;
    private ImageView imagePoster;

    private final String url = "http://image.tmdb.org/t/p/w185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        name = findViewById(R.id.moview_name_id);
        year = findViewById(R.id.year_id);
        time = findViewById(R.id.min_id);
        rating = findViewById(R.id.rating_id);
        plot = findViewById(R.id.synopsis_id);
        imagePoster = findViewById(R.id.movie_poster_id);

        Intent intent = getIntent();

        if(intent != null){
            Movie movie = intent.getParcelableExtra("MOVIEOBJECTPOSITION");
            Log.d("MOVIEOBJECTPOSITION", movie.getTitle());

            try{
               name.setText(movie.getTitle());
               year.setText(movie.getRelease_date());
               rating.setText(String.valueOf(movie.getVote_average()) + "/10");
               plot.setText(movie.getOverview());

                Uri uri = Uri.parse(url).buildUpon()
                        .appendEncodedPath(movie.getPoster_path())
                        .build();

                Picasso.get().load(uri).placeholder(R.drawable.ic_action_movie_error)
                        .error(R.drawable.ic_action_movie_error).into(imagePoster);


            } catch (NullPointerException e){
                Log.d("ERROR", e.getLocalizedMessage());
            }
        }
    }
}
