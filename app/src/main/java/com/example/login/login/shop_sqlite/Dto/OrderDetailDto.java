package com.example.login.login.shop_sqlite.Dto;

// OrderDetailDto.java
public class OrderDetailDto {
    private Integer productId;
    private String variantId;
    private Integer quantity;

    // Constructors, getters and setters
    public OrderDetailDto() {}
    public OrderDetailDto(Integer productId, String variantId, Integer quantity) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
