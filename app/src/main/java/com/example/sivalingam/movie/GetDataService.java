package com.example.sivalingam.movie;

/**
 * This is the retrofit interface which is used to get the data from the api.
 */

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    //This interface method is used to fetch the popular movies
    @GET("/3/movie/popular?api_key=INSERTKEYHERE")
    Call<OuterClass> getPopularMovies();

    //This interface method fetches the top rated movies
    @GET("/3/movie/top_rated?api_key=INSERTKEYHERE")
    Call<OuterClass> getRatingMovies();
}
