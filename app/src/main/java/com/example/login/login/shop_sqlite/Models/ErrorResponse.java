package com.example.login.login.shop_sqlite.Models;

public class ErrorResponse {
    private String message;
    private String error;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}