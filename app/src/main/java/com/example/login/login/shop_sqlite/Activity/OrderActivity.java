package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.R;
import com.example.login.login.shop_sqlite.Models.OrderRequest;
import com.example.login.login.shop_sqlite.Models.CartItemDto;
import com.example.login.login.shop_sqlite.Models.CartResponse;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private EditText etName, etPhone, etAddress;
    private Button btnPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Lấy userId từ SharedPreferences
            android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int userId = prefs.getInt("current_user_id", 0);
            // Lấy giỏ hàng từ backend
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            apiService.getCart(userId).enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<CartItemDto> cartItems = response.body().getItems();
                        double total = response.body().getAmountDue();
                        List<OrderRequest.OrderDetail> orderDetails = new ArrayList<>();
                        for (CartItemDto item : cartItems) {
                            orderDetails.add(new OrderRequest.OrderDetail(
                                item.getProductId(),
                                item.getProductName(),
                                item.getPrice() != null ? item.getPrice() : 0,
                                item.getQuantity()
                            ));
                        }
                        OrderRequest orderRequest = new OrderRequest(userId, name, phone, address, total, orderDetails);
                        apiService.placeOrder(orderRequest).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(OrderActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(OrderActivity.this, "Lỗi đặt hàng!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(OrderActivity.this, "Không lấy được giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {
                    Toast.makeText(OrderActivity.this, "Lỗi lấy giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
} 