package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.CreateProductCategoryDto;
import com.example.login.login.shop_sqlite.Dto.ProductCategoryResponseDto;
import com.example.login.login.shop_sqlite.R;

import retrofit2.*;

public class CreateCategoryActivity extends AppCompatActivity {

    private EditText edtTitle;
    private CheckBox chkIsDelete;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        edtTitle = findViewById(R.id.edtCategoryTitle);
        chkIsDelete = findViewById(R.id.chkIsDelete);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(view -> createCategory());
    }

    private void createCategory() {
        String title = edtTitle.getText().toString().trim();
        boolean isDelete = chkIsDelete.isChecked();

        if (title.isEmpty()) {
            Toast.makeText(this, "Tiêu đề không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateProductCategoryDto dto = new CreateProductCategoryDto(title, isDelete);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ProductCategoryResponseDto> call = apiService.createCategory(dto);


        call.enqueue(new Callback<ProductCategoryResponseDto>() {
            @Override
            public void onResponse(Call<ProductCategoryResponseDto> call, Response<ProductCategoryResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateCategoryActivity.this,
                            "Đã tạo: " + response.body().getProductCategoryTitle(),
                            Toast.LENGTH_LONG).show();

                    setResult(RESULT_OK);
                    finish();


                } else {
                    String errorMsg = "Lỗi khi tạo: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - Chi tiết: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                    }
                    Toast.makeText(CreateCategoryActivity.this,
                            errorMsg,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ProductCategoryResponseDto> call, Throwable t) {
                Toast.makeText(CreateCategoryActivity.this,
                        "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}