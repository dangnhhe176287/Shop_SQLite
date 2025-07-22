package com.example.login.login.shop_sqlite.Models;

public class ChangePasswordRequestDto {
    public int userId;
    public String oldPassword;
    public String newPassword;

    public ChangePasswordRequestDto() {
    }

    public ChangePasswordRequestDto(int userId, String oldPassword, String newPassword) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}