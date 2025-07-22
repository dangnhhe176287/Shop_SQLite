package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Models.ForgotPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginResponseDto;
import com.example.login.login.shop_sqlite.Models.RegisterRequestDto;

import com.example.login.login.shop_sqlite.Models.Product;
import com.example.login.login.shop_sqlite.Models.CartResponse;
import com.example.login.login.shop_sqlite.Models.CartItemDto;
import com.example.login.login.shop_sqlite.Models.OrderRequest;
import com.example.login.login.shop_sqlite.Models.OrderView;
import com.example.login.login.shop_sqlite.Models.ResetPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.VerifyOtpRequestDto;
import com.example.login.login.shop_sqlite.Models.ChangePasswordRequestDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

import com.example.login.login.shop_sqlite.Models.ForgotPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.VerifyOtpRequestDto;
import com.example.login.login.shop_sqlite.Models.ResetPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.ResetPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.VerifyOtpRequestDto;
import com.example.login.login.shop_sqlite.Models.UserDto;
import com.example.login.login.shop_sqlite.Models.UpdateUserDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/Auth/login")
    Call<LoginResponseDto> login(@Body LoginRequestDto loginRequest);

    @POST("api/Auth/register")
    Call<LoginResponseDto> register(@Body RegisterRequestDto registerRequest);

    @POST("api/Auth/forgot-password")
    Call<Void> forgotPassword(@Body ForgotPasswordRequestDto request);

    @POST("api/Auth/verify-otp")
    Call<Void> verifyOtp(@Body VerifyOtpRequestDto request);

    @POST("api/Auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequestDto request);

    @POST("api/Auth/change-password")
    Call<Void> changePassword(@Body ChangePasswordRequestDto request);

    @GET("api/Product")
    Call<List<Product>> getAllProducts(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("api/Product/search")
    Call<List<Product>> searchProducts(
            @Query("name") String name,
            @Query("category") String category,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("page") int page,
            @Query("pageSize") int pageSize);

    @GET("api/Product/{id}")
    Call<Product> getProductById(@Path("id") int productId);

    @GET("cart/{userId}")
    Call<CartResponse> getCart(@Path("userId") int userId);

    @POST("cart/{userId}/add")
    Call<Void> addToCart(@Path("userId") int userId, @Body CartItemDto item);

    @PUT("cart/{userId}/update")
    Call<Void> updateCartItem(@Path("userId") int userId, @Body CartItemDto item);

    @DELETE("cart/{userId}/remove/{productId}/{variantId}")
    Call<Void> removeFromCart(@Path("userId") int userId, @Path("productId") int productId,
            @Path("variantId") int variantId, @Query("variantAttributes") String variantAttributes);

    @DELETE("cart/{userId}/clear")
    Call<Void> clearCart(@Path("userId") int userId);

    @POST("api/Orders")
    Call<Void> placeOrder(@Body OrderRequest orderRequest);

    @GET("api/Orders")
    Call<List<OrderView>> getOrders(@Query("customerId") int customerId);

    @GET("api/Orders/{orderId}")
    Call<OrderView> getOrderDetail(@Path("orderId") int orderId);

    @GET("api/users/profile")
    Call<com.example.login.login.shop_sqlite.Models.UserProfileDto> getProfile(@Header("Authorization") String token);

    @GET("api/users/{id}")
    Call<com.example.login.login.shop_sqlite.Models.UserProfileDto> getUserById(@Path("id") int userId);

    @GET("api/Users/{id}")
    Call<UserDto> getUserByIdForEdit(@Path("id") int userId);

    @GET("api/Users")
    Call<List<UserDto>> getAllUsers();

    // @GET("api/Users/{id}")
    // Call<UserDto> getUserById(@Path("id") int id);

    @POST("api/Users")
    Call<UserDto> addUser(@Body UserDto user);

    @PUT("api/Users/{id}")
    Call<Void> updateUser(@Path("id") int id, @Body UpdateUserDto user);

    @DELETE("api/Users/{id}")
    Call<Void> deleteUser(@Path("id") int id);

}