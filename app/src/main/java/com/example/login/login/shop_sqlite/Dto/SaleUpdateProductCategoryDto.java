package com.example.login.login.shop_sqlite.Dto;


public class SaleUpdateProductCategoryDto {
    private String productCategoryTitle;
    private boolean isDelete;

    public SaleUpdateProductCategoryDto(String productCategoryTitle, boolean isDelete) {
        this.productCategoryTitle = productCategoryTitle;
        this.isDelete = isDelete;
    }

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
