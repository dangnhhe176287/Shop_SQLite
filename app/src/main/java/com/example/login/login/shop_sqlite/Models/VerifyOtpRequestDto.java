package com.example.login.login.shop_sqlite.Models;

public class VerifyOtpRequestDto {
    public String email;
    public String otpCode;

    public VerifyOtpRequestDto(String email, String otpCode) {
        this.email = email;
        this.otpCode = otpCode;
    }
}