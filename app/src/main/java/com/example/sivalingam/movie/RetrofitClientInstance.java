package com.example.sivalingam.movie;

/**
 * This is the retrofit instance. This takes in the URL, builds a retrofit variable and returns it to the
 * calling method or class.
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://api.themoviedb.org";

    //This is used to set the retrofit instance and return it.
    public static Retrofit getRetrofitInstance(){

        //If the retrofit is null
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        //return the Retrofit instance
        return retrofit;
    }
}
