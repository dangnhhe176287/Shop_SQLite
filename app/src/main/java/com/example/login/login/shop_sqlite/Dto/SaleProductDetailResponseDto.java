package com.example.login.login.shop_sqlite.Dto;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SaleProductDetailResponseDto {
    private int productId;
    private String name;
    private String description;
    private Integer productCategoryId;
    private String brand;
    private double basePrice;
    private String availableAttributes;
    private Integer status;
    private boolean isDelete;

    @SerializedName("createdAt")
    private String createdAtStr;
    @SerializedName("updatedAt")
    private String updatedAtStr;

    private transient Date createdAt;
    private transient Date updatedAt;

    @SerializedName("productImages")
    private List<SaleProductImageDto> productImages;

    @SerializedName("variants")
    private List<SaleProductVariantDto> variants;

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getProductCategoryId() { return productCategoryId; }
    public String getBrand() { return brand; }
    public double getBasePrice() { return basePrice; }
    public String getAvailableAttributes() { return availableAttributes; }
    public Integer getStatus() { return status; }
    public boolean isDelete() { return isDelete; }

    public Date getCreatedAt() {
        if (createdAt == null && createdAtStr != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault());
                createdAt = sdf.parse(createdAtStr);
            } catch (ParseException e) {
                System.err.println("Parse error for createdAtStr: " + createdAtStr);
                e.printStackTrace();
            }
        }
        return createdAt;
    }

    public Date getUpdatedAt() {
        if (updatedAt == null && updatedAtStr != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault());
                updatedAt = sdf.parse(updatedAtStr);
            } catch (ParseException e) {
                System.err.println("Parse error for updatedAtStr: " + updatedAtStr);
                e.printStackTrace();
            }
        }
        return updatedAt;
    }

    public List<SaleProductImageDto> getProductImages() { return productImages; }
    public List<SaleProductVariantDto> getVariants() { return variants; }

    public void setProductId(int productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setProductCategoryId(Integer productCategoryId) { this.productCategoryId = productCategoryId; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setAvailableAttributes(String availableAttributes) { this.availableAttributes = availableAttributes; }
    public void setStatus(Integer status) { this.status = status; }
    public void setIsDelete(boolean isDelete) { this.isDelete = isDelete; }
    public void setCreatedAtStr(String createdAtStr) {
        this.createdAtStr = createdAtStr;
        this.createdAt = null;
    }
    public void setUpdatedAtStr(String updatedAtStr) {
        this.updatedAtStr = updatedAtStr;
        this.updatedAt = null;
    }
    public void setProductImages(List<SaleProductImageDto> productImages) { this.productImages = productImages; }
    public void setVariants(List<SaleProductVariantDto> variants) { this.variants = variants; }
}