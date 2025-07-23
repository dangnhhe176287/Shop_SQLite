package com.example.login.login.shop_sqlite.Dto;

import com.google.gson.annotations.SerializedName;

public class SaleProductImageDto {
    @SerializedName("productImageId")
    private int productImageId;

    @SerializedName("imageUrl")
    private String imageUrl;

    public SaleProductImageDto(int productImageId, String imageUrl) {
        this.productImageId = productImageId;
        this.imageUrl = imageUrl;
    }

    public int getProductImageId() {
        return productImageId;
    }

    public void setProductImageId(int productImageId) {
        this.productImageId = productImageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
