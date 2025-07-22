package com.example.login.login.shop_sqlite.Dto;


public class ProductVariantDto {
    private String attributes;
    private String variants;

    public ProductVariantDto(String attributes, String variants) {
        this.attributes = attributes;
        this.variants = variants;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getVariants() {
        return variants;
    }

    public void setVariants(String variants) {
        this.variants = variants;
    }
}