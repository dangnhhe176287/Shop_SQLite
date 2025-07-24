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
import com.example.login.login.shop_sqlite.Models.UserProfileDto;
import com.example.login.login.shop_sqlite.Models.UserDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    // Auth APIs
    @POST("Auth/login")
    Call<LoginResponseDto> login(@Body LoginRequestDto loginRequest);

    @POST("Auth/register")
    Call<LoginResponseDto> register(@Body RegisterRequestDto registerRequest);

    @POST("Auth/forgot-password")
    Call<Void> forgotPassword(@Body ForgotPasswordRequestDto request);

    @POST("Auth/verify-otp")
    Call<Void> verifyOtp(@Body VerifyOtpRequestDto request);

    @POST("Auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequestDto request);

    // Product APIs
    @GET("Product")
    Call<List<Product>> getAllProducts(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("Product/search")
    Call<List<Product>> searchProducts(
            @Query("name") String name,
            @Query("category") String category,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("page") int page,
            @Query("pageSize") int pageSize);

    @GET("Product/{id}")
    Call<Product> getProductById(@Path("id") int productId);

    // Cart APIs
    @GET("cart/{userId}")
    Call<CartResponse> getCart(@Path("userId") int userId);

    @POST("cart/{userId}/add")
    Call<Void> addToCart(@Path("userId") int userId, @Body CartItemDto item);

    @PUT("cart/{userId}/update")
    Call<Void> updateCartItem(@Path("userId") int userId, @Body CartItemDto item);

    @DELETE("cart/{userId}/remove/{productId}/{variantId}")
    Call<Void> removeFromCart(@Path("userId") int userId, @Path("productId") int productId, @Path("variantId") int variantId);

    @DELETE("cart/{userId}/clear")
    Call<Void> clearCart(@Path("userId") int userId);

    // Order APIs
    @POST("orders")
    Call<Void> placeOrder(@Body OrderRequest orderRequest);

    @GET("orders")
    Call<List<OrderView>> getOrders(@Query("customerId") int customerId);

    @GET("orders/{orderId}")
    Call<OrderView> getOrderDetail(@Path("orderId") int orderId);

    // User APIs
    @GET("Users/{id}")
    Call<UserProfileDto> getUserById(@Path("id") int userId);

    @PUT("Users/{id}")
    Call<Void> updateUser(@Path("id") int id, @Body UserDto user);
}