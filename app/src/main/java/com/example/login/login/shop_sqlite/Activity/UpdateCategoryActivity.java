package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log; // Import Log
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.UpdateProductCategoryDto;
import com.example.login.login.shop_sqlite.Dto.ProductCategoryResponseDto;
import com.example.login.login.shop_sqlite.R;

import retrofit2.*;

public class UpdateCategoryActivity extends AppCompatActivity {

    private static final String TAG = "UpdateCategoryActivity";
    private EditText edtTitle;
    private CheckBox chkIsDelete;
    private Button btnUpdate;

    private int categoryId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);

        edtTitle = findViewById(R.id.edtCategoryTitle);
        chkIsDelete = findViewById(R.id.chkIsDelete);
        btnUpdate = findViewById(R.id.btnUpdate);

        apiService = ApiClient.getClient().create(ApiService.class);

        categoryId = getIntent().getIntExtra("categoryId", -1);

        if (categoryId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID danh mục để chỉnh sửa.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "categoryId là -1. Không thể chỉnh sửa.");
            finish();
            return;
        }

        fetchCategoryDetails(categoryId);

        btnUpdate.setOnClickListener(view -> updateCategory());
    }

    private void fetchCategoryDetails(int id) {
        Log.d(TAG, "Đang tải chi tiết danh mục với ID: " + id);
        apiService.getProductCategoryById(id).enqueue(new Callback<ProductCategoryResponseDto>() {
            @Override
            public void onResponse(Call<ProductCategoryResponseDto> call, Response<ProductCategoryResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductCategoryResponseDto category = response.body();
                    edtTitle.setText(category.getProductCategoryTitle());
                    chkIsDelete.setChecked(category.isIsDelete());

                    // Ẩn chkIsDelete nếu bạn không muốn người dùng điều khiển nó
                    // chkIsDelete.setVisibility(View.GONE);

                    Toast.makeText(UpdateCategoryActivity.this, "Đã tải thông tin danh mục.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Đã tải chi tiết: " + category.getProductCategoryTitle());
                } else {
                    String errorMsg = "Lỗi khi tải chi tiết danh mục: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - Chi tiết: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody khi tải chi tiết: " + e.getMessage(), e);
                    }
                    Toast.makeText(UpdateCategoryActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMsg);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ProductCategoryResponseDto> call, Throwable t) {
                String networkError = "Lỗi kết nối server khi tải chi tiết danh mục: " + t.getMessage();
                Log.e(TAG, networkError, t);
                Toast.makeText(UpdateCategoryActivity.this, networkError, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void updateCategory() {
        String title = edtTitle.getText().toString().trim();
        boolean isDelete = chkIsDelete.isChecked(); // Lấy trạng thái từ CheckBox

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Tiêu đề danh mục.", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProductCategoryDto dto = new UpdateProductCategoryDto(title, isDelete);


        Call<ProductCategoryResponseDto> call = apiService.updateCategory(categoryId, dto);

        Log.d(TAG, "Đang cập nhật danh mục ID: " + categoryId + ", Tiêu đề: " + title + ", IsDelete: " + isDelete);

        call.enqueue(new Callback<ProductCategoryResponseDto>() {
            @Override
            public void onResponse(Call<ProductCategoryResponseDto> call, Response<ProductCategoryResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UpdateCategoryActivity.this,
                            "Đã cập nhật: " + response.body().getProductCategoryTitle(),
                            Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = "Lỗi khi cập nhật danh mục: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - Chi tiết: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody khi cập nhật: " + e.getMessage(), e);
                    }
                    Toast.makeText(UpdateCategoryActivity.this,
                            errorMsg,
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ProductCategoryResponseDto> call, Throwable t) {
                String networkError = "Lỗi kết nối server khi cập nhật danh mục: " + t.getMessage();
                Log.e(TAG, networkError, t);
                Toast.makeText(UpdateCategoryActivity.this,
                        networkError, Toast.LENGTH_LONG).show();
            }
        });
    }
}