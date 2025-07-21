package com.example.login.login.shop_sqlite.Dto;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List; // Thêm import này
import java.util.Locale;

public class ProductResponseDto {
    private int productId;
    private String name;
    private String description;
    private Integer productCategoryId;
    private String brand;
    private double basePrice;
    private String availableAttributes; // Chuỗi JSON của {"size": ["S", "M"], "color": ["Red", "Blue"]}
    private Integer status;
    private boolean isDelete;

    @SerializedName("createdAt")
    private String createdAtStr;
    @SerializedName("updatedAt")
    private String updatedAtStr;

    // Sử dụng transient để Gson bỏ qua các trường Date này trong quá trình deserialize trực tiếp
    // Chúng ta sẽ parse thủ công từ các trường String
    private transient Date createdAt;
    private transient Date updatedAt;

    // Thêm các trường cho ProductImages và Variants
    private List<ProductImageDto> productImages;
    private List<ProductVariantDto> variants;

    // Constructor để khởi tạo đầy đủ các trường (Tùy chọn, Gson không yêu cầu nhưng tiện cho việc tạo đối tượng thủ công)
    public ProductResponseDto(int productId, String name, String description, Integer productCategoryId, String brand, double basePrice,
                              String availableAttributes, Integer status, boolean isDelete,
                              String createdAtStr, String updatedAtStr,
                              List<ProductImageDto> productImages, List<ProductVariantDto> variants) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.productCategoryId = productCategoryId;
        this.brand = brand;
        this.basePrice = basePrice;
        this.availableAttributes = availableAttributes;
        this.status = status;
        this.isDelete = isDelete;
        this.createdAtStr = createdAtStr;
        this.updatedAtStr = updatedAtStr;
        this.productImages = productImages;
        this.variants = variants;
        // Các trường Date transient sẽ được parse khi gọi getter
    }

    // --- Getters ---
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getProductCategoryId() { return productCategoryId; }
    public String getBrand() { return brand; }
    public double getBasePrice() { return basePrice; }
    public String getAvailableAttributes() { return availableAttributes; }
    public Integer getStatus() { return status; }
    public boolean isDelete() { return isDelete; }

    // Getter cho các trường Date, xử lý parse từ String
    public Date getCreatedAt() {
        if (createdAt == null && createdAtStr != null) {
            try {
                // Định dạng phù hợp với 7 chữ số thập phân (SSSSSSS)
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
                // Định dạng phù hợp với 7 chữ số thập phân (SSSSSSS)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault());
                updatedAt = sdf.parse(updatedAtStr);
            } catch (ParseException e) {
                System.err.println("Parse error for updatedAtStr: " + updatedAtStr);
                e.printStackTrace();
            }
        }
        return updatedAt;
    }

    // Getter cho các trường String ngày giờ gốc (nếu cần)
    public String getCreatedAtStr() { return createdAtStr; }
    public String getUpdatedAtStr() { return updatedAtStr; }

    // Getter cho ProductImages và Variants
    public List<ProductImageDto> getProductImages() { return productImages; }
    public List<ProductVariantDto> getVariants() { return variants; }

    // --- Setters ---
    public void setProductId(int productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setProductCategoryId(Integer productCategoryId) { this.productCategoryId = productCategoryId; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setAvailableAttributes(String availableAttributes) { this.availableAttributes = availableAttributes; }
    public void setStatus(Integer status) { this.status = status; }
    public void setDelete(boolean delete) { isDelete = delete; } // Sửa tên method từ setIsDelete thành setDelete cho chuẩn
    public void setCreatedAtStr(String createdAtStr) {
        this.createdAtStr = createdAtStr;
        this.createdAt = null; // Reset để ép buộc phân tích lại khi gọi getCreatedAt()
    }
    public void setUpdatedAtStr(String updatedAtStr) {
        this.updatedAtStr = updatedAtStr;
        this.updatedAt = null; // Reset để ép buộc phân tích lại khi gọi getUpdatedAt()
    }
    // Setter cho các trường Date transient (ít khi dùng trực tiếp, chủ yếu cho deserialization của Gson)
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    // Setter cho ProductImages và Variants
    public void setProductImages(List<ProductImageDto> productImages) { this.productImages = productImages; }
    public void setVariants(List<ProductVariantDto> variants) { this.variants = variants; }
}