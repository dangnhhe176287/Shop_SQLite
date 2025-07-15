package com.example.login.login.shop_sqlite.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ProductVariant {
    @SerializedName("variantId")
    private Integer variantId;
    
    @SerializedName("productId")
    private int productId;
    
    @SerializedName("attributes")
    private String attributes;
    
    @SerializedName("variants")
    private List<Map<String, Object>> variants;

    // Constructors
    public ProductVariant() {}

    public ProductVariant(Integer variantId, int productId, String attributes, 
                         List<Map<String, Object>> variants) {
        this.variantId = variantId;
        this.productId = productId;
        this.attributes = attributes;
        this.variants = variants;
    }

    // Getters and Setters
    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public List<Map<String, Object>> getVariants() {
        return variants;
    }

    public void setVariants(List<Map<String, Object>> variants) {
        this.variants = variants;
    }
} 