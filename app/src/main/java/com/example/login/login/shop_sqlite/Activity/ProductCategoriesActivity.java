package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Import này cho dialog xác nhận
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Adapter.CategoryAdapter; // Import CategoryAdapter
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.ProductCategoryResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;

public class ProductCategoriesActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryActionListener {

    private ListView listView;
    private CategoryAdapter adapter;
    private List<ProductCategoryResponseDto> categoryList;
    private Button btnAddCategory;
    private ApiService apiService;

    private static final int REQUEST_CODE_CREATE_CATEGORY = 1;
    private static final int REQUEST_CODE_UPDATE_CATEGORY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_categories);

        listView = findViewById(R.id.listViewCategories);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(this, categoryList, this);
        listView.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(ProductCategoriesActivity.this, CreateCategoryActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_CATEGORY); // Dùng startActivityForResult
        });
        fetchCategories();
    }

    private void fetchCategories() {
        Log.d("ProductCategoriesActivity", "Bắt đầu tải danh mục...");
        apiService.getAllProductCategories().enqueue(new Callback<List<ProductCategoryResponseDto>>() {
            @Override
            public void onResponse(Call<List<ProductCategoryResponseDto>> call, Response<List<ProductCategoryResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    for (ProductCategoryResponseDto dto : response.body()) {
                        if (!dto.isIsDelete()) {
                            categoryList.add(dto);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("ProductCategoriesActivity", "Đã tải " + categoryList.size() + " danh mục. Adapter đã được cập nhật.");
                } else {
                    String errorBody = "Không có thông tin lỗi";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("ProductCategoriesActivity", "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(ProductCategoriesActivity.this, "Lỗi khi tải danh mục: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e("ProductCategoriesActivity", "Lỗi tải danh mục: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<ProductCategoryResponseDto>> call, Throwable t) {
                Log.e("ProductCategoriesActivity", "Lỗi kết nối khi tải danh mục", t);
                Toast.makeText(ProductCategoriesActivity.this, "Không thể kết nối đến máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(int categoryId) {
        Log.d("ProductCategoriesActivity", "Chỉnh sửa danh mục với ID: " + categoryId);
        Intent intent = new Intent(ProductCategoriesActivity.this, UpdateCategoryActivity.class);
        intent.putExtra("categoryId", categoryId);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_CATEGORY);
    }

    @Override
    public void onDeleteClick(int categoryId) {
        Log.d("ProductCategoriesActivity", "Yêu cầu xóa danh mục với ID: " + categoryId);

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa danh mục này không? Thao tác này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteCategory(categoryId);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCategory(int categoryId) {
        apiService.deleteCategory(categoryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductCategoriesActivity.this, "Đã xóa danh mục thành công!", Toast.LENGTH_SHORT).show();
                    fetchCategories();
                    Log.d("ProductCategoriesActivity", "Danh mục ID " + categoryId + " đã được xóa thành công.");
                } else {
                    String errorMsg = "Lỗi khi xóa danh mục: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + errorBodyString;
                            Toast.makeText(ProductCategoriesActivity.this, "Lỗi server: " + errorBodyString, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProductCategoriesActivity.this, "Lỗi server không xác định: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("ProductCategoriesActivity", "Lỗi khi đọc errorBody khi xóa", e);
                        Toast.makeText(ProductCategoriesActivity.this, "Lỗi server, không đọc được chi tiết lỗi.", Toast.LENGTH_LONG).show();
                    }
                    Log.e("ProductCategoriesActivity", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String networkError = "Không thể kết nối đến máy chủ khi xóa danh mục: " + t.getMessage();
                Log.e("ProductCategoriesActivity", networkError, t);
                Toast.makeText(ProductCategoriesActivity.this, networkError, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CREATE_CATEGORY) {
                fetchCategories();
                Toast.makeText(this, "Danh mục mới đã được tạo thành công!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_CODE_UPDATE_CATEGORY) {
                fetchCategories();
                Toast.makeText(this, "Danh mục đã được cập nhật.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}