package com.example.sivalingam.movie;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;

public class ReviewActivity extends AppCompatActivity implements ReviewAdapter.ReviewAdapterOnClickListener {

    //variables
    private RecyclerView mRecyclerView;
    private ReviewAdapter mReviewAdapter;
    List<Reviews> reviewsList;
    private SwipeRefreshLayout reviewLayout;
    private int movieId;
    private TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //setting up the recycler view
        mRecyclerView = findViewById(R.id.review_recycler_id);

        //setting up UI variables
        errorTV = findViewById(R.id.review_error_tv_id);

        //Setting up swipe refresh layout
        reviewLayout = findViewById(R.id.swipe_review);

        reviewsList = new ArrayList<>();

        //Linear layout manager
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //Setting linear layout manager to recycler view
        mRecyclerView.setLayoutManager(manager);

        //Set size of recycler view to fixed
        mRecyclerView.setHasFixedSize(true);

        //Initializing review adapter
        mReviewAdapter = new ReviewAdapter(this);

        //Setting back button for review activity
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get intent
        final Intent intent = getIntent();

        //If intent is not null
        if(intent != null){

            //Get int extra from the intent
            movieId = intent.getIntExtra("MOVIEIDREVIEW", -1);

            //if movieId is not -1
            if(movieId != -1){

                //If user is online
                if(isOnline()){

                    //Set error textview ti invisible
                    errorTV.setVisibility(View.INVISIBLE);

                    //Set recycler view to visible
                    mRecyclerView.setVisibility(View.VISIBLE);

                    //fetch data from the internet
                    fetchData(movieId);
                }

                //If user is not online
                else{

                    //Set visibility of recycler view to invisible
                    mRecyclerView.setVisibility(View.INVISIBLE);

                    //Set visibility of error text view to visible
                    errorTV.setVisibility(View.VISIBLE);
                }
            }
        }

        //Set OnRefreshListener for the swipe refresh layout
        reviewLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //If user is online
                if(isOnline()){

                    //Set refreshing to false
                    reviewLayout.setRefreshing(false);

                    //Set error textview as invisible
                    errorTV.setVisibility(View.INVISIBLE);

                    //Set visibility of recycler view to visible
                    mRecyclerView.setVisibility(View.VISIBLE);

                    //Fetch data from the internet
                    fetchData(intent.getIntExtra("MOVIEIDREVIEW", -1));
                } else {

                    //Set refreshing to false
                    reviewLayout.setRefreshing(false);

                    //Set error textview to visible
                    errorTV.setVisibility(View.VISIBLE);

                    //Set recycler view to invisible
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
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

    /**
     *
     * @param id
     *
     * This function is used to fetch the review data from the internet
     */
    private void fetchData(int id){

        //Initialize the service
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        retrofit2.Call<OuterReview> call = service.getReviews(id);

        call.enqueue(new Callback<OuterReview>() {
            @Override
            public void onResponse(Call<OuterReview> call, Response<OuterReview> response) {

                //Data fetch was successful
                //Clear review list
                reviewsList.clear();

                //Get data from response and set it to reviews list
                reviewsList.addAll(response.body().getReviews());

                //Pass data to Review adapter
                mReviewAdapter.setReviewsList(reviewsList);

                //Set adapter to recycler view
                mRecyclerView.setAdapter(mReviewAdapter);
            }

            @Override
            public void onFailure(Call<OuterReview> call, Throwable t) {

            }
        });
    }

    /**
     *
     * @param id
     *
     * This overriden method is used to open the web browser to see the review in more detail
     */
    @Override
    public void onClick(int id) {

        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewsList.get(id).getUrl()));

        try{
            this.startActivity(webIntent);
        } catch (ActivityNotFoundException a){
            Toast.makeText(this, "Website cannot be opened", Toast.LENGTH_SHORT).show();
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
