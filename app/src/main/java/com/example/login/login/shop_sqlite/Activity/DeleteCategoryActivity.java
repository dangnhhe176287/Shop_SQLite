package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.R;

import retrofit2.*;

public class DeleteCategoryActivity extends AppCompatActivity {

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
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<Void> call = apiService.deleteCategory(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DeleteCategoryActivity.this, "Đã xóa danh mục ID: " + id, Toast.LENGTH_LONG).show();
                    edtCategoryId.setText("");
                } else if (response.code() == 404) {
                    Toast.makeText(DeleteCategoryActivity.this, "Không tìm thấy danh mục", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DeleteCategoryActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DeleteCategoryActivity.this, "Lỗi kết nối server", Toast.LENGTH_LONG).show();
            }
        });
    }
}
