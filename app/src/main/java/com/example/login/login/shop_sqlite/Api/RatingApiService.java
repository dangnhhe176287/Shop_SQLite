package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Models.Rating;
import com.example.login.login.shop_sqlite.Models.RatingRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RatingApiService {
    @GET("api/rating/product/{productId}")
    Call<List<Rating>> getRatingsByProduct(@Path("productId") int productId);

    @POST("api/rating")
    Call<Void> addRating(@Body RatingRequest rating);
} 