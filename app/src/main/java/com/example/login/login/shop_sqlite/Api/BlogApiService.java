package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Models.Blog;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;

public interface BlogApiService {
    @GET("api/blog/all")
    Call<List<Blog>> getAllBlogs();

    @GET("api/blog/load")
    Call<List<Blog>> getBlogsPaged(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("api/blog/{id}")
    Call<Blog> getBlogById(@Path("id") int id);

    @POST("api/blog")
    Call<Void> addBlog(@Body Blog blog);

    @PUT("api/blog/{id}")
    Call<Void> updateBlog(@Path("id") int id, @Body Blog blog);

    @DELETE("api/blog/{id}")
    Call<Void> deleteBlog(@Path("id") int id, @Query("confirm") boolean confirm);

    @Multipart
    @POST("api/blog/upload-thumbnail")
    Call<ResponseBody> uploadThumbnail(@Part MultipartBody.Part file);

    @PUT("api/blog/increase-view/{id}")
    Call<Void> increaseView(@Path("id") int id);
}