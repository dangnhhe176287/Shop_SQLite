package com.example.login.login.shop_sqlite.Dto;

import java.util.List;

public class SaleUpdateProductDto {
    private String name;
    private String description;
    private Integer productCategoryId;
    private String brand;
    private double basePrice;
    private String availableAttributes;
    private Integer status;
    private boolean isDelete;
    private List<SaleUpdateProductImageDto> productImages;
    private List<SaleUpdateProductVariantDto> variants;

    public SaleUpdateProductDto(String name, String description, Integer productCategoryId,
                                String brand, double basePrice, String availableAttributes,
                                Integer status, boolean isDelete,
                                List<SaleUpdateProductImageDto> productImages,
                                List<SaleUpdateProductVariantDto> variants) {
        this.name = name;
        this.description = description;
        this.productCategoryId = productCategoryId;
        this.brand = brand;
        this.basePrice = basePrice;
        this.availableAttributes = availableAttributes;
        this.status = status;
        this.isDelete = isDelete;
        this.productImages = productImages;
        this.variants = variants;
    }
}
