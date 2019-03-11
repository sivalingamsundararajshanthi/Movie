package com.example.sivalingam.movie;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This call is used for getting the data from the API.
 */

public class FetchData {

    /**
     *
     * @param sort
     * @return
     *
     * This public static method is used to make the actual fetch based on the sort criteria specified and returns
     * a mutable live data object which has a list of movies.
     */
    public static MutableLiveData<List<Movie>> fetchData(boolean sort){

        Log.d("testfornetwork", "here1");

        //Initialize the mutable live data
        final MutableLiveData<List<Movie>> movies = new MutableLiveData<>();

        //Set the Retrofit instance
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        retrofit2.Call<OuterClass> call;

        //This gets information from the API based on the sort criteria specified.
        if(sort){
            //popular movies are loaded
           call  = service.getPopularMovies();
            Log.d("testfornetwork", "popular");
        } else {
            //highest rated movies are loaded
            call = service.getRatingMovies();
            Log.d("testfornetwork", "highest");
        }

        call.enqueue(new Callback<OuterClass>() {
            @Override
            public void onResponse(Call<OuterClass> call, Response<OuterClass> response) {

                //The API fetch was successful
                try{
                    //set the movie list inside the mutable live data
                    movies.setValue(response.body().getMovieList());

                } catch(NullPointerException e){
                    Log.d("NULLPOINTEREXCEPTION", "NPE");
                }
            }

            @Override
            public void onFailure(Call<OuterClass> call, Throwable t) {
                //API fetch was unsuccessful.
                movies.setValue(null);
                Log.d("APICALLUNSUCCESSFUL", "UNSUCCESSFUL");
            }
        });

        return movies;
    }
}
