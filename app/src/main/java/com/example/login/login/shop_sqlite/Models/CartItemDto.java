package com.example.login.login.shop_sqlite.Models;

public class CartItemDto {
    private int productId;
    private int quantity;
    private String productName;
    private Double price;
    private String imageUrl;
    private int variantId;
    private String variantAttributes;

    public CartItemDto(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public String getVariantAttributes() { return variantAttributes; }
    public void setVariantAttributes(String variantAttributes) { this.variantAttributes = variantAttributes; }
} 