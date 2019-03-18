package com.example.sivalingam.movie;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;

public class ReviewActivity extends AppCompatActivity implements ReviewAdapter.ReviewAdapterOnClickListener {

    private RecyclerView mRecyclerView;
    private ReviewAdapter mReviewAdapter;
    List<Reviews> reviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mRecyclerView = findViewById(R.id.review_recycler_id);

        reviewsList = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);

        mReviewAdapter = new ReviewAdapter(this);

        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();


        if(intent != null){
            int movieId = intent.getIntExtra("MOVIEIDREVIEW", -1);

            if(movieId != -1){
                if(isOnline())
                    fetchData(movieId);
                else{
                    showSnackBar(movieId);
                }
            }
        }
    }

    private void showSnackBar(final int id){
        View view = findViewById(R.id.review_activity_id);
        Snackbar snackbar = Snackbar.make(view, "No internet", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Try again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    fetchData(id);
                } else {
                    showSnackBar(id);
                }
            }
        });

        snackbar.show();
    }

    private void fetchData(int id){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        retrofit2.Call<OuterReview> call = service.getReviews(id);

        call.enqueue(new Callback<OuterReview>() {
            @Override
            public void onResponse(Call<OuterReview> call, Response<OuterReview> response) {
                reviewsList = response.body().getReviews();
                mReviewAdapter.setReviewsList(reviewsList);
                mRecyclerView.setAdapter(mReviewAdapter);
            }

            @Override
            public void onFailure(Call<OuterReview> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(int id) {

        Log.d("review", "review clicked");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewsList.get(id).getUrl()));

        try{
            this.startActivity(webIntent);
        } catch (ActivityNotFoundException a){
            Toast.makeText(this, "Website cannot be opened", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
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
