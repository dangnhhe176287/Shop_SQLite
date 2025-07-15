package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Models.LoginRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginResponseDto;
import com.example.login.login.shop_sqlite.Models.RegisterRequestDto;

import com.example.login.login.shop_sqlite.Models.Product;
import com.example.login.login.shop_sqlite.Models.CartResponse;
import com.example.login.login.shop_sqlite.Models.CartItemDto;
import com.example.login.login.shop_sqlite.Models.OrderRequest;
import com.example.login.login.shop_sqlite.Models.OrderView;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

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

    @GET("cart/{userId}")
    Call<CartResponse> getCart(@Path("userId") int userId);

    @POST("cart/{userId}/add")
    Call<Void> addToCart(@Path("userId") int userId, @Body CartItemDto item);

    @PUT("cart/{userId}/update")
    Call<Void> updateCartItem(@Path("userId") int userId, @Body CartItemDto item);

    @DELETE("cart/{userId}/remove/{productId}")
    Call<Void> removeFromCart(@Path("userId") int userId, @Path("productId") int productId);

    @POST("api/orders")
    Call<Void> placeOrder(@Body OrderRequest orderRequest);

    @GET("api/orders")
    Call<List<OrderView>> getOrders(@Query("customerId") int customerId);

    @GET("api/orders/{orderId}")
    Call<OrderView> getOrderDetail(@Path("orderId") int orderId);
}