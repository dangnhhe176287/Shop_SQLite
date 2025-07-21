package com.example.login.login.shop_sqlite.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateProductDto {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("productCategoryId")
    private Integer productCategoryId;

    @SerializedName("brand")
    private String brand;

    @SerializedName("basePrice")
    private double basePrice;

    @SerializedName("availableAttributes")
    private String availableAttributes;

    @SerializedName("status")
    private Integer status;

    @SerializedName("isDelete")
    private boolean isDelete;

    @SerializedName("productImages")
    private List<ProductImageDto> productImages;

    @SerializedName("variants")
    private List<ProductVariantDto> variants;

    // Constructor
    public CreateProductDto(String name, String description, Integer productCategoryId, String brand,
                            double basePrice, String availableAttributes, Integer status, boolean isDelete,
                            List<ProductImageDto> productImages, List<ProductVariantDto> variants) {
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

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getProductCategoryId() { return productCategoryId; }
    public void setProductCategoryId(Integer productCategoryId) { this.productCategoryId = productCategoryId; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public String getAvailableAttributes() { return availableAttributes; }
    public void setAvailableAttributes(String availableAttributes) { this.availableAttributes = availableAttributes; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public boolean isDelete() { return isDelete; }
    public void setIsDelete(boolean isDelete) { this.isDelete = isDelete; }
    public List<ProductImageDto> getProductImages() { return productImages; }
    public void setProductImages(List<ProductImageDto> productImages) { this.productImages = productImages; }
    public List<ProductVariantDto> getVariants() { return variants; }
    public void setVariants(List<ProductVariantDto> variants) { this.variants = variants; }
}