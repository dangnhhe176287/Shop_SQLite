package com.example.login.login.shop_sqlite.Models;

public class Rating {
    private int ratingId;
    private int productId;
    private int userId;
    private int score;
    private String createdAt;
    private boolean isVisible;

    public int getRatingId() { return ratingId; }
    public void setRatingId(int ratingId) { this.ratingId = ratingId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
} 