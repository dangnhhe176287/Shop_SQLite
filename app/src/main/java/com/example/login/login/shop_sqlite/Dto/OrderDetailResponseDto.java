package com.example.login.login.shop_sqlite.Dto;

import java.math.BigDecimal;

public class OrderDetailResponseDto {
    private Integer productId;
    private String variantId;
    private int quantity;
    private BigDecimal price;
    private String productName;

    public OrderDetailResponseDto() {
    }


    public OrderDetailResponseDto(Integer productId, String variantId, int quantity, BigDecimal price, String productName) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
    }


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}