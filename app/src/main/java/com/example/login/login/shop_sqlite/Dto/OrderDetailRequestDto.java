package com.example.login.login.shop_sqlite.Dto;

public class OrderDetailRequestDto {
    private Integer productId;
    private String variantId;
    private int quantity;

    public OrderDetailRequestDto(Integer productId, String variantId, int quantity) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
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
}