package com.example.login.login.shop_sqlite.Models;

public class AuthResponse {
    private String message;
    private String token;
    private int userId;
    private String roleName;
    private String userName;

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
