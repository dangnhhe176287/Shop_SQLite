package com.example.login.login.shop_sqlite.Models;

public class RatingRequest {
    private int productId;
    private int userId;
    private int score;

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
} 