package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Adapter.CartAdapter;
import com.example.login.login.shop_sqlite.Models.CartItem;
import com.example.login.login.shop_sqlite.Models.CartManager;
import com.example.login.login.shop_sqlite.R;
import com.example.login.login.shop_sqlite.Models.CartResponse;
import com.example.login.login.shop_sqlite.Models.CartItemDto;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import java.util.List;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView tvTotalAmount;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, OrderActivity.class);
            startActivityForResult(intent, 1001); // Sử dụng startActivityForResult để nhận kết quả
        });

        // Lấy userId hiện tại từ SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("current_user_id", 0);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getCart(userId).enqueue(new retrofit2.Callback<CartResponse>() {
            @Override
            public void onResponse(retrofit2.Call<CartResponse> call, retrofit2.Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<CartItemDto> cartItems = response.body().getItems();
                    cartAdapter = new CartAdapter(cartItems);
                    recyclerView.setAdapter(cartAdapter);
                    // Hiển thị tổng tiền
                    double total = response.body().getAmountDue();
                    tvTotalAmount.setText("Tổng tiền: " + formatPrice(total));
                    // Đăng ký callback cập nhật tổng tiền khi thay đổi
                    cartAdapter.setOnCartChangedListener(() -> reloadCart());
                } else {
                    android.widget.Toast.makeText(CartActivity.this, "Không lấy được giỏ hàng", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<CartResponse> call, Throwable t) {
                android.widget.Toast.makeText(CartActivity.this, "Lỗi kết nối giỏ hàng", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thêm hàm formatPrice vào CartActivity
    private String formatPrice(double price) {
        if (price >= 1_000_000) {
            return String.format("$%.2fM", price / 1_000_000);
        } else if (price >= 1_000) {
            if (price % 1000 == 0) {
                return String.format("$%.0fk", price / 1000);
            } else {
                return String.format("$%.2fk", price / 1000);
            }
        } else {
            return String.format("$%.2f", price);
        }
    }

    // Thêm hàm reloadCart để gọi lại API lấy giỏ hàng và cập nhật tổng tiền
    private void reloadCart() {
        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("current_user_id", 0);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getCart(userId).enqueue(new retrofit2.Callback<CartResponse>() {
            @Override
            public void onResponse(retrofit2.Call<CartResponse> call, retrofit2.Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double total = response.body().getAmountDue();
                    tvTotalAmount.setText("Tổng tiền: " + formatPrice(total));
                    
                    // Cập nhật lại adapter nếu có sản phẩm
                    if (response.body().getItems() != null && !response.body().getItems().isEmpty()) {
                        cartAdapter.updateCartItems(response.body().getItems());
                    } else {
                        // Nếu giỏ hàng trống, hiển thị thông báo
                        recyclerView.setVisibility(View.GONE);
                        findViewById(R.id.emptyCartMessage).setVisibility(View.VISIBLE);
                        tvTotalAmount.setText("Tổng tiền: $0.00");
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<CartResponse> call, Throwable t) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Refresh cart khi quay về từ OrderActivity
            reloadCart();
        }
    }
} 