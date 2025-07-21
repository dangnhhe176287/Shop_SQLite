package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Models.Review;
import com.example.login.login.shop_sqlite.Models.ReviewRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewApiService {
    @GET("api/review/product/{productId}")
    Call<List<Review>> getReviewsByProduct(@Path("productId") int productId);

    @POST("api/review")
    Call<Void> addReview(@Body ReviewRequest review);
} 