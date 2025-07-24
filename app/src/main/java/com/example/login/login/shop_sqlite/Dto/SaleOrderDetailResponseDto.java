package com.example.login.login.shop_sqlite.Dto;

public class SaleOrderDetailResponseDto {
    private Integer productId;
    private String variantId;
    private int quantity;
    private double price;
    private String productName;
    private String variantAttributes;
    public SaleOrderDetailResponseDto(Integer productId, String variantId, int quantity, double price, String productName, String variantAttributes) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
        this.variantAttributes = variantAttributes;
    }

    public Integer getProductId() { return productId; }
    public String getVariantId() { return variantId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getProductName() { return productName; }
    public String getVariantAttributes() { return variantAttributes; }
}