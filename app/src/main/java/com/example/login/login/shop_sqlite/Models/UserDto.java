package com.example.login.login.shop_sqlite.Models;

import java.io.Serializable;

public class UserDto implements Serializable {
    public int userId;
    public int roleId;
    public String email;
    public String password;
    public String phone;
    public String userName;
    public String dateOfBirth;
    public String address;
    public String createDate;
    public int status;
    public boolean isDelete;
}