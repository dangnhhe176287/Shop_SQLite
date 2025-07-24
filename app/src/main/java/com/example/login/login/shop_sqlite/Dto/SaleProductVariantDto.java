package com.example.login.login.shop_sqlite.Dto;


public class SaleProductVariantDto {
    private int variantId;          // thêm trường này
    private String attributes;
    private String variants;

    public int getVariantId() {
        return variantId;
    }

    public String getAttributes() {
        return attributes;
    }

    public String getVariants() {
        return variants;
    }
}
