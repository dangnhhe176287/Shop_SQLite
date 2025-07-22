// com.example.login.login.shop_sqlite.Activity.ProductDetailActivity.java
package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.ProductResponseDto;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson; // Import Gson
import com.google.gson.reflect.TypeToken; // Import TypeToken

import java.lang.reflect.Type; // Import Type
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map; // Import Map

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";

    // Update TextViews to match new DTO fields and their display logic
    private TextView tvProductId, tvProductName, tvProductDescription,
            tvProductCategory, tvProductBrand, tvBasePrice, tvAvailableAttributes,
            tvProductStatus, tvProductIsDelete, tvProductCreatedDate, tvProductLastUpdated;
    // private ImageView ivProductImage; // Uncomment if you add an ImageView and use Glide/Picasso

    private ApiService apiService;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize TextViews based on the new layout structure (assuming you'll update activity_product_detail.xml accordingly)
        tvProductId = findViewById(R.id.tvProductId);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductCategory = findViewById(R.id.tvProductCategory);
        tvProductBrand = findViewById(R.id.tvProductBrand); // New TextView for brand
        tvBasePrice = findViewById(R.id.tvProductPrice); // Renamed from tvProductPrice to tvBasePrice if needed in layout
        tvAvailableAttributes = findViewById(R.id.tvProductAvailableAttributes); // New TextView for attributes
        tvProductStatus = findViewById(R.id.tvProductStatus); // New TextView for status
        tvProductIsDelete = findViewById(R.id.tvProductIsDelete); // New TextView for isDelete
        tvProductCreatedDate = findViewById(R.id.tvProductCreatedDate);
        tvProductLastUpdated = findViewById(R.id.tvProductLastUpdated);
        // ivProductImage = findViewById(R.id.ivProductImage); // Uncomment if you add an ImageView

        apiService = ApiClient.getClient().create(ApiService.class);

        productId = getIntent().getIntExtra("productId", -1);

        if (productId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID sản phẩm để xem chi tiết.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "productId là -1. Không thể xem chi tiết.");
            finish();
            return;
        }

        fetchProductDetails(productId);
    }

    private void fetchProductDetails(int id) {
        Log.d(TAG, "Đang tải chi tiết sản phẩm với ID: " + id);
        apiService.getProductById(id).enqueue(new Callback<ProductResponseDto>() {
            @Override
            public void onResponse(Call<ProductResponseDto> call, Response<ProductResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponseDto product = response.body();
                    Log.d(TAG, "Đã tải thành công chi tiết sản phẩm: " + product.getName()); // Use getName()
                    displayProductDetails(product);
                } else {
                    String errorMsg = "Lỗi khi tải chi tiết sản phẩm: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - Chi tiết: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody: " + e.getMessage(), e);
                        errorMsg += " - Không thể đọc chi tiết lỗi.";
                    }
                    Toast.makeText(ProductDetailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onResponse Error khi tải chi tiết: " + errorMsg);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ProductResponseDto> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi tải chi tiết sản phẩm: " + t.getMessage(), t);
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối server khi tải chi tiết sản phẩm.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void displayProductDetails(ProductResponseDto product) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        tvProductId.setText("ID Sản phẩm: #" + product.getProductId());
        tvProductName.setText("Tên sản phẩm: " + (product.getName() != null ? product.getName() : "N/A")); // Use getName()
        tvProductDescription.setText("Mô tả: " + (product.getDescription() != null && !product.getDescription().isEmpty() ? product.getDescription() : "Không có mô tả"));
        tvProductCategory.setText("Danh mục ID: " + (product.getProductCategoryId() != null ? product.getProductCategoryId() : "N/A"));
        tvProductBrand.setText("Thương hiệu: " + (product.getBrand() != null ? product.getBrand() : "N/A")); // Display Brand
        tvBasePrice.setText(String.format(Locale.getDefault(), "Giá cơ bản: %.2f VNĐ", (product.getBasePrice()))); // Use getBasePrice()

        // Handle availableAttributes (JSON string)
        if (product.getAvailableAttributes() != null && !product.getAvailableAttributes().isEmpty()) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
                Map<String, List<String>> attributesMap = gson.fromJson(product.getAvailableAttributes(), type);
                StringBuilder sb = new StringBuilder("Thuộc tính: ");
                boolean firstAttribute = true;
                for (Map.Entry<String, List<String>> entry : attributesMap.entrySet()) {
                    if (!firstAttribute) {
                        sb.append("; ");
                    }
                    sb.append(entry.getKey()).append(": ").append(android.text.TextUtils.join(", ", entry.getValue()));
                    firstAttribute = false;
                }
                tvAvailableAttributes.setText(sb.toString());
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi phân tích AvailableAttributes: " + e.getMessage());
                tvAvailableAttributes.setText("Thuộc tính: Lỗi phân tích dữ liệu");
            }
        } else {
            tvAvailableAttributes.setText("Thuộc tính: N/A");
        }

        tvProductStatus.setText("Trạng thái: " + (product.getStatus() != null ? product.getStatus() : "N/A"));
        tvProductIsDelete.setText("Đã xóa: " + (product.isDelete() ? "Có" : "Không"));

        if (product.getCreatedAt() != null) {
            tvProductCreatedDate.setText("Ngày tạo: " + dateFormat.format(product.getCreatedAt()));
        } else {
            tvProductCreatedDate.setText("Ngày tạo: N/A");
        }

        if (product.getUpdatedAt() != null) {
            tvProductLastUpdated.setText("Cập nhật lần cuối: " + dateFormat.format(product.getUpdatedAt()));
        } else {
            tvProductLastUpdated.setText("Cập nhật lần cuối: N/A");
        }

        // --- Handle Product Images (if you want to display them) ---
        // You'll need an ImageView in your layout and an image loading library (e.g., Glide).
        /*
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            // Assuming ProductImageDto has a getImageUrl() method and you want to display the first image
            String imageUrl = product.getProductImages().get(0).getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.default_product_image); // Set a default image
            }
        } else {
            ivProductImage.setImageResource(R.drawable.default_product_image); // Set a default image
        }
        */

        // --- Handle Product Variants (if you want to display them) ---
        // You might need a dynamic way to add TextViews or a RecyclerView for variants
        /*
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            // Example: Log variants or display them in a dedicated view
            for (ProductVariantDto variant : product.getVariants()) {
                Log.d(TAG, "Variant ID: " + variant.getVariantId() +
                          ", SKU: " + variant.getSKU() +
                          ", Price: " + variant.getPrice());
                // Add logic to display these, e.g., in a LinearLayout or another RecyclerView
            }
        }
        */
    }
}