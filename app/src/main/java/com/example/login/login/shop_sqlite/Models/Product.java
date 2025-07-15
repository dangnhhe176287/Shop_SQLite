package com.example.login.login.shop_sqlite.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Product {
    @SerializedName("productId")
    private int productId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("brand")
    private String brand;
    
    @SerializedName("basePrice")
    private double basePrice;
    
    @SerializedName("availableAttributes")
    private String availableAttributes;
    
    @SerializedName("categoryId")
    private int categoryId;
    
    @SerializedName("categoryName")
    private String categoryName;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("variants")
    private List<ProductVariant> variants;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

    // Constructors
    public Product() {}

    public Product(int productId, String name, String description, String brand, 
                   double basePrice, String categoryName, List<String> images) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.basePrice = basePrice;
        this.categoryName = categoryName;
        this.imageUrl = images.get(0); // Assuming the first image is the main image
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getAvailableAttributes() {
        return availableAttributes;
    }

    public void setAvailableAttributes(String availableAttributes) {
        this.availableAttributes = availableAttributes;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper method to format price
    public String getFormattedPrice() {
        return String.format("%,.0f VNĐ", basePrice);
    }
} 