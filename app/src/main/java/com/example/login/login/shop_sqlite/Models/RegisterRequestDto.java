package com.example.login.login.shop_sqlite.Models;

public class RegisterRequestDto {
    public String email;
    public String password;
    public String userName;
    public String phone;
    public String dateOfBirth;
    public String address;

    public RegisterRequestDto(String email, String password, String userName, String phone, String dateOfBirth,
            String address) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}
