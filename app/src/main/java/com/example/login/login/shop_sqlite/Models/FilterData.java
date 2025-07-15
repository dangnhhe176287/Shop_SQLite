package com.example.login.login.shop_sqlite.Models;

public class FilterData {
    private double minPrice;
    private double maxPrice;
    private String category;
    private String brand;
    private boolean isActive;

    public FilterData() {
        this.minPrice = 0;
        this.maxPrice = 10000000; // 10 million VND
        this.category = "";
        this.brand = "";
        this.isActive = false;
    }

    public FilterData(double minPrice, double maxPrice, String category, String brand) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.category = category;
        this.brand = brand;
        this.isActive = true;
    }

    // Getters and Setters
    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Helper methods
    public void clear() {
        this.minPrice = 0;
        this.maxPrice = 10000000;
        this.category = "";
        this.brand = "";
        this.isActive = false;
    }

    public boolean hasPriceFilter() {
        return minPrice > 0 || maxPrice < 10000000;
    }

    public boolean hasCategoryFilter() {
        return category != null && !category.isEmpty();
    }

    public boolean hasBrandFilter() {
        return brand != null && !brand.isEmpty();
    }

    public String getPriceRangeText() {
        if (minPrice == 0 && maxPrice == 10000000) {
            return "All prices";
        } else if (minPrice == 0) {
            return String.format("Under %,.0f VNĐ", maxPrice);
        } else if (maxPrice == 10000000) {
            return String.format("Over %,.0f VNĐ", minPrice);
        } else {
            return String.format("%,.0f - %,.0f VNĐ", minPrice, maxPrice);
        }
    }

    @Override
    public String toString() {
        return "FilterData{" +
                "minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", isActive=" + isActive +
                '}';
    }
} 