package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.CreateOrderDto; // DTO để tạo đơn hàng
import com.example.login.login.shop_sqlite.Dto.OrderDetailRequestDto; // DTO cho chi tiết đơn hàng
import com.example.login.login.shop_sqlite.Models.Order; // Dùng Order model cho phản hồi
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderActivity extends AppCompatActivity {

    private static final String TAG = "CreateOrderActivity";
    private EditText edtCustomerId, edtPaymentMethodId, edtOrderNote;
    private EditText edtProductId, edtQuantity, edtVariantId;
    private Button btnCreateOrder;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        edtCustomerId = findViewById(R.id.edtCustomerId);
        edtPaymentMethodId = findViewById(R.id.edtPaymentMethodId);
        edtOrderNote = findViewById(R.id.edtOrderNote);

        edtProductId = findViewById(R.id.edtProductId);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtVariantId = findViewById(R.id.edtVariantId);

        btnCreateOrder = findViewById(R.id.btnCreateOrder);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnCreateOrder.setOnClickListener(v -> createOrder());
    }

    private void createOrder() {
        // 1. Lấy dữ liệu từ UI
        String customerIdStr = edtCustomerId.getText().toString().trim();
        String paymentMethodIdStr = edtPaymentMethodId.getText().toString().trim();
        String orderNote = edtOrderNote.getText().toString().trim();

        String productIdStr = edtProductId.getText().toString().trim();
        String quantityStr = edtQuantity.getText().toString().trim();
        String variantIdStr = edtVariantId.getText().toString().trim();

        if (customerIdStr.isEmpty() || paymentMethodIdStr.isEmpty() ||
                productIdStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc (Customer ID, Payment Method ID, Product ID, Quantity).", Toast.LENGTH_LONG).show();
            return;
        }

        int customerId;
        int paymentMethodId;
        int productId;
        int quantity;

        try {
            customerId = Integer.parseInt(customerIdStr);
            paymentMethodId = Integer.parseInt(paymentMethodIdStr);
            productId = Integer.parseInt(productIdStr);
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID và Số lượng phải là số hợp lệ.", Toast.LENGTH_LONG).show();
            return;
        }

        // Validate giá trị như Razor Page
        if (customerId <= 0) {
            Toast.makeText(this, "Customer ID phải lớn hơn 0.", Toast.LENGTH_LONG).show();
            return;
        }
        if (paymentMethodId <= 0) {
            Toast.makeText(this, "Payment Method ID phải lớn hơn 0.", Toast.LENGTH_LONG).show();
            return;
        }
        if (productId <= 0) {
            Toast.makeText(this, "Product ID phải lớn hơn 0.", Toast.LENGTH_LONG).show();
            return;
        }
        if (quantity <= 0) {
            Toast.makeText(this, "Quantity phải lớn hơn 0.", Toast.LENGTH_LONG).show();
            return;
        }

        String finalVariantId = variantIdStr.isEmpty() ? null : variantIdStr;

        List<OrderDetailRequestDto> orderDetails = new ArrayList<>();
        orderDetails.add(new OrderDetailRequestDto(productId, finalVariantId, quantity));

        CreateOrderDto createOrderDto = new CreateOrderDto(
                customerId,
                paymentMethodId,
                null,
                orderNote.isEmpty() ? null : orderNote,
                orderDetails
        );

        Log.d(TAG, "Gửi CreateOrderDto: " + new com.google.gson.Gson().toJson(createOrderDto));

        apiService.createOrder(createOrderDto).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateOrderActivity.this, "Tạo đơn hàng thành công! Mã: " + response.body().getOrderId(), Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = "Lỗi khi tạo đơn hàng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String rawErrorBody = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + rawErrorBody;
                            Log.e(TAG, "Lỗi phản hồi API (" + response.code() + "): " + rawErrorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody: " + e.getMessage(), e);
                        errorMsg += " - Không thể đọc chi tiết lỗi.";
                    }
                    Toast.makeText(CreateOrderActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(CreateOrderActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}