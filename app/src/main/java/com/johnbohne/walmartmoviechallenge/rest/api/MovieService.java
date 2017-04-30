package com.johnbohne.walmartmoviechallenge.rest.api;

import com.johnbohne.walmartmoviechallenge.rest.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by john on 4/27/17.
 */

public interface MovieService {
    @GET("search/movie?api_key=0dadcf4266b00367f5cf262e3906b57d&language=en-US&include_adult=true")
    Call<MovieResponse> searchMovie(@Query("query") String query, @Query("page") int page);
}
