package com.example.login.login.shop_sqlite.Models;

public class ReviewRequest {
    private int productId;
    private int userId;
    private String content;

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
} 