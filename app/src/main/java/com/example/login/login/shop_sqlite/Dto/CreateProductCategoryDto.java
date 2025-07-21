package com.example.login.login.shop_sqlite.Dto;

public class CreateProductCategoryDto {
    private String productCategoryTitle;
    private boolean isDelete;

    public CreateProductCategoryDto(String productCategoryTitle, boolean isDelete) {
        this.productCategoryTitle = productCategoryTitle;
        this.isDelete = isDelete;
    }

    // Getters and Setters
    public String getProductCategoryTitle() {
        return productCategoryTitle;
    }

    public void setProductCategoryTitle(String productCategoryTitle) {
        this.productCategoryTitle = productCategoryTitle;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }
}
