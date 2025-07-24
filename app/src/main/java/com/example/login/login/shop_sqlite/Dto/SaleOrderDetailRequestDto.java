package com.example.login.login.shop_sqlite.Dto;

import com.google.gson.JsonObject;

public class SaleOrderDetailRequestDto {
    private Integer productId;
    private String variantId;
    private int quantity;
    private JsonObject variantAttributes;

    public SaleOrderDetailRequestDto(Integer productId, String variantId, int quantity) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.variantAttributes = null;
    }

    public SaleOrderDetailRequestDto(Integer productId, String variantId, int quantity, JsonObject variantAttributes) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.variantAttributes = variantAttributes;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getVariantId() {
        return variantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public JsonObject getVariantAttributes() {
        return variantAttributes;
    }
}