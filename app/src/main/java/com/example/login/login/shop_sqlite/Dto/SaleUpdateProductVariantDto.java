package com.example.login.login.shop_sqlite.Dto;

public class SaleUpdateProductVariantDto {
    private int variantId;
    private String attributes;
    private String variants;

    public SaleUpdateProductVariantDto() {
    }

    public SaleUpdateProductVariantDto(int variantId, String attributes, String variants) {
        this.variantId = variantId;
        this.attributes = attributes;
        this.variants = variants;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
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