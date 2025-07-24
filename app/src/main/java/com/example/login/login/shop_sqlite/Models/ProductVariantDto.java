package com.example.login.login.shop_sqlite.Models;

import java.util.List;
import java.util.Map;

public class ProductVariantDto {
    public int variantId;
    public int productId;
    public String attributes; 
    public List<Map<String, Object>> variants; 
} 