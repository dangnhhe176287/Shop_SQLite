package com.example.login.login.shop_sqlite.Dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SaleCreateProductDto {
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
    private List<SaleCreateProductImageDto> productImages;

    @SerializedName("variants")
    private List<SaleCreateProductVariantDto> variants;

    public SaleCreateProductDto(String name, String description, Integer productCategoryId, String brand,
                                double basePrice, String availableAttributes, Integer status, boolean isDelete,
                                List<SaleCreateProductImageDto> productImages, List<SaleCreateProductVariantDto> variants) {
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

    // Getter
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getProductCategoryId() { return productCategoryId; }
    public String getBrand() { return brand; }
    public double getBasePrice() { return basePrice; }
    public String getAvailableAttributes() { return availableAttributes; }
    public Integer getStatus() { return status; }
    public boolean isDelete() { return isDelete; }
    public List<SaleCreateProductImageDto> getProductImages() { return productImages; }
    public List<SaleCreateProductVariantDto> getVariants() { return variants; }
}
