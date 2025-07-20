package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Models.Blog;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BlogApiService {
    @GET("api/blog/all")
    Call<List<Blog>> getAllBlogs();

    @GET("api/blog/load")
    Call<List<Blog>> getBlogsPaged(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("api/blog/{id}")
    Call<Blog> getBlogById(@Path("id") int id);
} 