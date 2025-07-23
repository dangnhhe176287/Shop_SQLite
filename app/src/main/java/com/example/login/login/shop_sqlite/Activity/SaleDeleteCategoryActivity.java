package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.R;

import retrofit2.*;

public class SaleDeleteCategoryActivity extends AppCompatActivity {

    private EditText edtCategoryId;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_category);

        edtCategoryId = findViewById(R.id.edtCategoryId);
        btnDelete = findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(view -> deleteCategory());
    }

    private void deleteCategory() {
        String idStr = edtCategoryId.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);
        SaleApiService saleApiService = ApiClient.getClient().create(SaleApiService.class);

        Call<Void> call = saleApiService.deleteCategory(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SaleDeleteCategoryActivity.this, "Đã xóa danh mục ID: " + id, Toast.LENGTH_LONG).show();
                    edtCategoryId.setText("");
                } else if (response.code() == 404) {
                    Toast.makeText(SaleDeleteCategoryActivity.this, "Không tìm thấy danh mục", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SaleDeleteCategoryActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SaleDeleteCategoryActivity.this, "Lỗi kết nối server", Toast.LENGTH_LONG).show();
            }
        });
    }
}
