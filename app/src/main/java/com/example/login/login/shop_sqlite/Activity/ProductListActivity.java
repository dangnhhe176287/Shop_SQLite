package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Adapter.ProductAdapter;
import com.example.login.login.shop_sqlite.Dto.ProductResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.*;

import retrofit2.*;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<ProductResponseDto> productList;
    private ApiService apiService;
    private Button btnCreateNewProduct;

    private static final int REQUEST_CODE_EDIT_PRODUCT = 1;
    private static final int REQUEST_CODE_CREATE_PRODUCT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerView = findViewById(R.id.recyclerProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnCreateNewProduct = findViewById(R.id.btnCreateNewProduct);

        btnCreateNewProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, CreateProductActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_PRODUCT);
        });

        fetchProducts();
    }
    private void fetchProducts() {
        Log.d("ProductListActivity", "Bắt đầu tải lại danh sách sản phẩm...");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<ProductResponseDto>> call = apiService.getAllProducts();

        call.enqueue(new Callback<List<ProductResponseDto>>() {
            @Override
            public void onResponse(Call<List<ProductResponseDto>> call, Response<List<ProductResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d("ProductListActivity", "Đã tải lại " + productList.size() + " sản phẩm. Adapter đã được cập nhật."); // Thêm log này
                } else {
                    String errorBody = "Không có thông tin lỗi";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("ProductListActivity", "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(ProductListActivity.this, "Lỗi khi tải sản phẩm: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e("ProductListActivity", "Lỗi tải sản phẩm: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponseDto>> call, Throwable t) {
                Log.e("ProductListActivity", "Lỗi kết nối khi tải sản phẩm", t);
                Toast.makeText(ProductListActivity.this, "Không thể kết nối đến máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(int productId) {
        Log.d("ProductListActivity", "Chỉnh sửa sản phẩm với ID: " + productId);
        Intent intent = new Intent(ProductListActivity.this, EditProductActivity.class);
        intent.putExtra("productId", productId);
        startActivityForResult(intent, REQUEST_CODE_EDIT_PRODUCT);
    }

    @Override
    public void onDeleteClick(int productId) {
        Log.d("ProductListActivity", "Yêu cầu xóa sản phẩm với ID: " + productId);
        apiService.deleteProduct(productId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductListActivity.this, "Đã xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    fetchProducts();
                    Log.d("ProductListActivity", "Sản phẩm ID " + productId + " đã được xóa thành công.");
                } else {
                    String errorMsg = "Lỗi khi xóa sản phẩm: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + errorBodyString;
                            Toast.makeText(ProductListActivity.this, "Lỗi server: " + errorBodyString, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProductListActivity.this, "Lỗi server không xác định: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("ProductListActivity", "Lỗi khi đọc errorBody khi xóa", e);
                        Toast.makeText(ProductListActivity.this, "Lỗi server, không đọc được chi tiết lỗi.", Toast.LENGTH_LONG).show();
                    }
                    Log.e("ProductListActivity", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String networkError = "Không thể kết nối đến máy chủ khi xóa sản phẩm: " + t.getMessage();
                Log.e("ProductListActivity", networkError, t);
                Toast.makeText(ProductListActivity.this, networkError, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_PRODUCT && resultCode == RESULT_OK) {
            fetchProducts();
            Toast.makeText(this, "Sản phẩm đã được cập nhật.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_CREATE_PRODUCT && resultCode == RESULT_OK) {

            fetchProducts();
            Toast.makeText(this, "Sản phẩm mới đã được tạo thành công!", Toast.LENGTH_SHORT).show();
        }
    }
}