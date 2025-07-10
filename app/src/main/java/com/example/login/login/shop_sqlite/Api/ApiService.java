package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Models.LoginRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginResponseDto;
import com.example.login.login.shop_sqlite.Models.RegisterRequestDto;
import com.example.login.login.shop_sqlite.Models.ForgotPasswordRequestDto;
import com.example.login.login.shop_sqlite.Models.VerifyOtpRequestDto;
import com.example.login.login.shop_sqlite.Models.ResetPasswordRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
}