package com.example.login.login.shop_sqlite.Models;

public class ResetPasswordRequestDto {
    public String email;
    public String otpCode;
    public String newPassword;

    public ResetPasswordRequestDto(String email, String otpCode, String newPassword) {
        this.email = email;
        this.otpCode = otpCode;
        this.newPassword = newPassword;
    }
}