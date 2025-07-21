package com.example.login.login.shop_sqlite.Activity;

import android.widget.EditText;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.CreateOrderDto;
import com.example.login.login.shop_sqlite.Dto.OrderDetailDto;
import com.example.login.login.shop_sqlite.Models.Order;
import com.example.login.login.shop_sqlite.R;

import java.util.*;
import retrofit2.*;

public class CreateOrderActivity extends AppCompatActivity {
    private EditText edtCustomerId, edtPaymentMethodId, edtOrderNote;
    private EditText edtProductId, edtVariantId, edtQuantity;
    private Button btnSubmitOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        edtCustomerId = findViewById(R.id.edtCustomerId);
        edtPaymentMethodId = findViewById(R.id.edtPaymentMethodId);
        edtOrderNote = findViewById(R.id.edtOrderNote);
        edtProductId = findViewById(R.id.edtProductId);
        edtVariantId = findViewById(R.id.edtVariantId);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);

        btnSubmitOrder.setOnClickListener(v -> submitOrder());
    }

    private void submitOrder() {
        int customerId = Integer.parseInt(edtCustomerId.getText().toString());
        int paymentMethodId = Integer.parseInt(edtPaymentMethodId.getText().toString());
        String note = edtOrderNote.getText().toString();

        int productId = Integer.parseInt(edtProductId.getText().toString());
        String variantId = edtVariantId.getText().toString();
        int quantity = Integer.parseInt(edtQuantity.getText().toString());

        List<OrderDetailDto> details = new ArrayList<>();
        details.add(new OrderDetailDto(productId, variantId.isEmpty() ? null : variantId, quantity));

        CreateOrderDto dto = new CreateOrderDto(customerId, paymentMethodId, note, details);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Order> call = apiService.createOrder(dto);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateOrderActivity.this, "Tạo đơn hàng thành công. Mã đơn: " + response.body().getOrderId(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CreateOrderActivity.this, "Thất bại! Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(CreateOrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("CreateOrder", "onFailure: ", t);
            }
        });
    }
}