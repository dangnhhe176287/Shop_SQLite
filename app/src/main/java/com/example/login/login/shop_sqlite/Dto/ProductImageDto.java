package com.example.login.login.shop_sqlite.Dto;

import com.google.gson.annotations.SerializedName;

public class ProductImageDto {
    @SerializedName("imageUrl")
    private String imageUrl;

    public ProductImageDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}