package com.example.login.login.shop_sqlite.Dto;
import com.google.gson.annotations.SerializedName;

public class SaleUpdateProductImageDto {
    @SerializedName("productImageId")
    private int productImageId;

    @SerializedName("imageUrl")
    private String imageUrl;

    public SaleUpdateProductImageDto(int productImageId, String imageUrl) {
        this.productImageId = productImageId;
        this.imageUrl = imageUrl;
    }

    public int getProductImageId() {
        return productImageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
