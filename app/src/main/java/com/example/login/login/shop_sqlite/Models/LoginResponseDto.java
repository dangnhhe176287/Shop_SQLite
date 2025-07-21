package com.example.login.login.shop_sqlite.Models;

import com.google.gson.annotations.SerializedName;

public class LoginResponseDto {
    public String message;
    public String token;
    public int userId;
    @SerializedName("roleName")
    public String roleName;
    public String userName;
}