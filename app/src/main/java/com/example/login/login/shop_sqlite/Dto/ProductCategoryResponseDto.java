package com.example.login.login.shop_sqlite.Dto;

public class ProductCategoryResponseDto {
    private int productCategoryId;
    private String productCategoryTitle;
    private boolean isDelete;

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public String getProductCategoryTitle() {
        return productCategoryTitle;
    }

    public boolean isIsDelete() {
        return isDelete;
    }
}