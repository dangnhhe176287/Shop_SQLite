package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Models.ForgotPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginResponseDto;
import com.example.login.login.shop_sqlite.Models.RegisterRequestDto;
import com.example.login.login.shop_sqlite.Models.ForgotPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.VerifyOtpRequestDto;
import com.example.login.login.shop_sqlite.Models.ResetPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.ResetPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.VerifyOtpRequestDto;
import com.example.login.login.shop_sqlite.Models.UserDto;

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

    @GET("api/Users")
    Call<List<UserDto>> getAllUsers();

    @GET("api/Users/{id}")
    Call<UserDto> getUserById(@Path("id") int id);

    @POST("api/Users")
    Call<UserDto> addUser(@Body UserDto user);

    @PUT("api/Users/{id}")
    Call<Void> updateUser(@Path("id") int id, @Body UserDto user);

    @DELETE("api/Users/{id}")
    Call<Void> deleteUser(@Path("id") int id);
}