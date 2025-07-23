package com.example.login.login.shop_sqlite.Dto;

import com.google.gson.annotations.SerializedName;

public class SaleCreateProductImageDto {
    @SerializedName("imageUrl")
    private String imageUrl;

    public SaleCreateProductImageDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
