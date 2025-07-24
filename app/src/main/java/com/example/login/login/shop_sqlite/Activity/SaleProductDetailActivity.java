// com.example.login.login.shop_sqlite.Activity.ProductDetailActivity.java
package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Dto.SaleProductResponseDto;
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

public class SaleProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";

    private TextView tvProductId, tvProductName, tvProductDescription,
            tvProductCategory, tvProductBrand, tvBasePrice, tvAvailableAttributes,
            tvProductStatus, tvProductIsDelete, tvProductCreatedDate, tvProductLastUpdated;

    private SaleApiService saleApiService;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_product_detail);

         tvProductId = findViewById(R.id.tvProductId);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductCategory = findViewById(R.id.tvProductCategory);
        tvProductBrand = findViewById(R.id.tvProductBrand);
        tvBasePrice = findViewById(R.id.tvProductPrice);
        tvAvailableAttributes = findViewById(R.id.tvProductAvailableAttributes);
        tvProductStatus = findViewById(R.id.tvProductStatus);
        tvProductIsDelete = findViewById(R.id.tvProductIsDelete);
        tvProductCreatedDate = findViewById(R.id.tvProductCreatedDate);
        tvProductLastUpdated = findViewById(R.id.tvProductLastUpdated);

        saleApiService = ApiClient.getClient().create(SaleApiService.class);

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
        saleApiService.getProductById(id).enqueue(new Callback<SaleProductResponseDto>() {
            @Override
            public void onResponse(Call<SaleProductResponseDto> call, Response<SaleProductResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SaleProductResponseDto product = response.body();
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
                    Toast.makeText(SaleProductDetailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onResponse Error khi tải chi tiết: " + errorMsg);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SaleProductResponseDto> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi tải chi tiết sản phẩm: " + t.getMessage(), t);
                Toast.makeText(SaleProductDetailActivity.this, "Lỗi kết nối server khi tải chi tiết sản phẩm.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void displayProductDetails(SaleProductResponseDto product) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        tvProductId.setText("ID Sản phẩm: #" + product.getProductId());
        tvProductName.setText("Tên sản phẩm: " + (product.getName() != null ? product.getName() : "N/A")); // Use getName()
        tvProductDescription.setText("Mô tả: " + (product.getDescription() != null && !product.getDescription().isEmpty() ? product.getDescription() : "Không có mô tả"));
        tvProductCategory.setText("Danh mục ID: " + (product.getProductCategoryId() != null ? product.getProductCategoryId() : "N/A"));
        tvProductBrand.setText("Thương hiệu: " + (product.getBrand() != null ? product.getBrand() : "N/A")); // Display Brand
        tvBasePrice.setText(String.format(Locale.getDefault(), "Giá cơ bản: %.2f VNĐ", (product.getBasePrice()))); // Use getBasePrice()

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
    }
}