package com.example.login.login.shop_sqlite.Models;

public class Review {
    private int reviewId;
    private int productId;
    private int userId;
    private String content;
    private String createdAt;
    private boolean isVisible;

    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
} 