package com.example.sivalingam.movie;

/**
 * This is the retrofit interface which is used to get the data from the api.
 */

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetDataService {

    //This interface method is used to fetch the popular movies
    @GET("/3/movie/popular?api_key=")
    Call<OuterClass> getPopularMovies();

    //This interface method fetches the top rated movies
    @GET("/3/movie/top_rated?api_key=")
    Call<OuterClass> getRatingMovies();

    //This interface method is used to fetch trailers of the selected movie
    @GET("/3/movie/{id}/videos?api_key=")
    Call<OuterVideo> getVideos(@Path("id") int id);

    @GET("/3/movie/{id}/reviews?api_key=")
    Call<OuterReview> getReviews(@Path("id") int id);
}
