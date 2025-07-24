package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Dto.SaleCreateOrderDto;
import com.example.login.login.shop_sqlite.Dto.SaleOrderDetailRequestDto;
import com.example.login.login.shop_sqlite.Models.SaleOrder;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleCreateOrderActivity extends AppCompatActivity {
    private static final String TAG = "CreateOrderActivity";

    private EditText edtCustomerId, edtPaymentMethodId, edtOrderNote, edtShippingAddress, edtShippingFee;
    private LinearLayout orderDetailsContainer;
    private Button btnAddProduct, btnCreateOrder;

    private SaleApiService saleApiService;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saie_activity_create_order);

        initViews();
        saleApiService = ApiClient.getClient().create(SaleApiService.class);

        btnAddProduct.setOnClickListener(v -> addOrderDetailField());
        btnCreateOrder.setOnClickListener(v -> createOrder());

        addOrderDetailField();
    }

    private void initViews() {
        edtCustomerId = findViewById(R.id.edtCustomerId);
        edtPaymentMethodId = findViewById(R.id.edtPaymentMethodId);
        edtOrderNote = findViewById(R.id.edtOrderNote);
        edtShippingAddress = findViewById(R.id.edtShippingAddress);
        edtShippingFee = findViewById(R.id.edtShippingFee);

        orderDetailsContainer = findViewById(R.id.orderDetailsContainer);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
    }

    private void addOrderDetailField() {
        View detailView = LayoutInflater.from(this).inflate(R.layout.sale_item_order_detail, orderDetailsContainer, false);

        EditText edtItemProductId = detailView.findViewById(R.id.edtItemProductId);
        EditText edtItemVariantId = detailView.findViewById(R.id.edtItemVariantId);
        EditText edtItemQuantity = detailView.findViewById(R.id.edtItemQuantity);
        EditText edtItemVariantAttributes = detailView.findViewById(R.id.edtItemVariantAttributes);
        Button btnRemoveItem = detailView.findViewById(R.id.btnRemoveItem);

        btnRemoveItem.setOnClickListener(v -> orderDetailsContainer.removeView(detailView));

        orderDetailsContainer.addView(detailView);
    }


    private void createOrder() {
        try {
            String customerIdStr = edtCustomerId.getText().toString().trim();
            String paymentMethodIdStr = edtPaymentMethodId.getText().toString().trim();
            String orderNote = edtOrderNote.getText().toString().trim();
            String shippingAddress = edtShippingAddress.getText().toString().trim();
            String shippingFeeStr = edtShippingFee.getText().toString().trim();

            if (customerIdStr.isEmpty() || paymentMethodIdStr.isEmpty() || shippingAddress.isEmpty() || shippingFeeStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc.", Toast.LENGTH_LONG).show();
                return;
            }

            int customerId = Integer.parseInt(customerIdStr);
            Integer paymentMethodId = Integer.parseInt(paymentMethodIdStr);
            double shippingFee = Double.parseDouble(shippingFeeStr);

            List<SaleOrderDetailRequestDto> orderDetails = new ArrayList<>();

            for (int i = 0; i < orderDetailsContainer.getChildCount(); i++) {
                View detailView = orderDetailsContainer.getChildAt(i);

                EditText edtItemProductId = detailView.findViewById(R.id.edtItemProductId);
                EditText edtItemVariantId = detailView.findViewById(R.id.edtItemVariantId);
                EditText edtItemQuantity = detailView.findViewById(R.id.edtItemQuantity);
                EditText edtItemVariantAttributes = detailView.findViewById(R.id.edtItemVariantAttributes);

                String productIdStr = edtItemProductId.getText().toString().trim();
                String variantId = edtItemVariantId.getText().toString().trim();
                String quantityStr = edtItemQuantity.getText().toString().trim();
                String variantAttributesStr = edtItemVariantAttributes.getText().toString().trim();

                if (productIdStr.isEmpty() && quantityStr.isEmpty()) {
                    continue;
                }

                Integer productId = null;
                int quantity = 0;
                JsonObject variantAttributesJson = null;

                if (!productIdStr.isEmpty()) {
                    try {
                        productId = Integer.parseInt(productIdStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "ID sản phẩm không hợp lệ ở mục " + (i + 1), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(this, "ID sản phẩm không được để trống ở mục " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!quantityStr.isEmpty()) {
                    try {
                        quantity = Integer.parseInt(quantityStr);
                        if (quantity <= 0) {
                            Toast.makeText(this, "Số lượng sản phẩm phải lớn hơn 0 ở mục " + (i + 1), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Số lượng không hợp lệ ở mục " + (i + 1), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(this, "Số lượng không được để trống ở mục " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!variantAttributesStr.isEmpty()) {
                    try {
                        Map<String, String> attrsMap = new HashMap<>();
                        String[] pairs = variantAttributesStr.split(",");
                        for (String pair : pairs) {
                            String[] keyValue = pair.split(":");
                            if (keyValue.length == 2) {
                                attrsMap.put(keyValue[0].trim(), keyValue[1].trim());
                            } else {
                                Toast.makeText(this, "Định dạng thuộc tính biến thể không hợp lệ ở mục " + (i + 1) + ". Ví dụ: size:One Size,color:Beige", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        variantAttributesJson = gson.toJsonTree(attrsMap).getAsJsonObject();
                    } catch (JsonSyntaxException e) {
                        Toast.makeText(this, "Định dạng JSON thuộc tính biến thể không hợp lệ ở mục " + (i + 1) + ". " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                orderDetails.add(new SaleOrderDetailRequestDto(productId, variantId.isEmpty() ? null : variantId, quantity, variantAttributesJson));
            }

            if (orderDetails.isEmpty()) {
                Toast.makeText(this, "Đơn hàng phải có ít nhất một sản phẩm được thêm và điền đầy đủ.", Toast.LENGTH_LONG).show();
                return;
            }

            SaleCreateOrderDto dto = new SaleCreateOrderDto(
                    customerId,
                    paymentMethodId,
                    orderNote,
                    shippingAddress,
                    shippingFee,
                    orderDetails
            );

            Log.d(TAG, "Json gửi: " + gson.toJson(dto));

            saleApiService.createOrder(dto).enqueue(new Callback<SaleOrder>() {
                @Override
                public void onResponse(Call<SaleOrder> call, Response<SaleOrder> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SaleCreateOrderActivity.this,
                                "Tạo đơn hàng thành công: " + gson.toJson(response.body()),
                                Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Response: " + gson.toJson(response.body()));
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi đọc error body: " + e.getMessage());
                        }
                        Toast.makeText(SaleCreateOrderActivity.this,
                                "Lỗi khi tạo đơn hàng: " + response.code() + " - " + errorBody,
                                Toast.LENGTH_LONG).show();
                        Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);
                    }
                }

                @Override
                public void onFailure(Call<SaleOrder> call, Throwable t) {
                    Toast.makeText(SaleCreateOrderActivity.this,
                            "Không thể kết nối: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi mạng: " + t.getMessage(), t);
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập dữ liệu số hợp lệ cho các trường chính (Customer ID, Payment Method ID, Phí vận chuyển).", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Lỗi định dạng số ở cấp độ đơn hàng: " + e.getMessage(), e);
        } catch (Exception ex) {
            Toast.makeText(this, "Có lỗi xảy ra: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Lỗi chung: " + ex.getMessage(), ex);
        }
    }
}