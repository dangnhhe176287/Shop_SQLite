package com.example.login.login.shop_sqlite.Models;

import java.util.List;
import java.util.Map;

public class ProductVariantDto {
    public int variantId;
    public int productId;
    public String attributes; // JSON string, có thể parse thành Map<String, List<String>>
    public List<Map<String, Object>> variants; // Danh sách các biến thể cụ thể (size, color, price, stock,...)
} 