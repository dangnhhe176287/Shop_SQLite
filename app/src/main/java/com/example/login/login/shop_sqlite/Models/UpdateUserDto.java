package com.example.login.login.shop_sqlite.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class UpdateUserDto implements Serializable {
    @SerializedName("userId")
    public int userId;
    
    @SerializedName("roleId")
    public int roleId;
    
    @SerializedName("email")
    public String email;
    
    @SerializedName("password")
    public String password; // Có thể null
    
    @SerializedName("phone")
    public String phone;
    
    @SerializedName("userName")
    public String userName;
    
    @SerializedName("dateOfBirth")
    public String dateOfBirth;
    
    @SerializedName("address")
    public String address;
    
    @SerializedName("createDate")
    public String createDate;
    
    @SerializedName("status")
    public int status;
    
    @SerializedName("isDelete")
    public boolean isDelete;
} 