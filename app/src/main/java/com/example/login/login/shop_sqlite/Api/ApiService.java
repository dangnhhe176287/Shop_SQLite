package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Models.LoginRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginResponseDto;
import com.example.login.login.shop_sqlite.Models.RegisterRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/Auth/login")
    Call<LoginResponseDto> login(@Body LoginRequestDto loginRequest);

    @POST("api/Auth/register")
    Call<LoginResponseDto> register(@Body RegisterRequestDto registerRequest);
}