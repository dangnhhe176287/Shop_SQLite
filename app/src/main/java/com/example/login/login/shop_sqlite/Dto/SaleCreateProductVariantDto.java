package com.example.login.login.shop_sqlite.Dto;

import com.google.gson.annotations.SerializedName;

public class SaleCreateProductVariantDto {
    @SerializedName("attributes")
    private String attributes;

    @SerializedName("variants")
    private String variants;

    public SaleCreateProductVariantDto(String attributes, String variants) {
        this.attributes = attributes;
        this.variants = variants;
    }

    public String getAttributes() {
        return attributes;
    }

    public String getVariants() {
        return variants;
    }
}
