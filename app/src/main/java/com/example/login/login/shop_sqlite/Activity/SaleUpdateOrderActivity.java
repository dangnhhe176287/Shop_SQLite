package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter; // Import ArrayAdapter
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner; // Import Spinner
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Dto.SaleOrderDetailRequestDto;
import com.example.login.login.shop_sqlite.Dto.SaleOrderDetailResponseDto;
import com.example.login.login.shop_sqlite.Dto.SaleOrderResponseDto;
import com.example.login.login.shop_sqlite.Dto.SaleUpdateOrderDto;
import com.example.login.login.shop_sqlite.Models.SaleOrder;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName; // Import này để đảm bảo mapping chính xác
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleUpdateOrderActivity extends AppCompatActivity {

    private static final String TAG = "UpdateOrderActivity";
    private EditText edtCustomerId, edtPaymentMethodId, edtOrderNote, edtShippingAddress;
    private Spinner spinnerOrderStatus;
    private LinearLayout orderDetailsContainer;
    private Button btnAddProduct, btnUpdateOrder;

    private int orderId;
    private SaleApiService saleApiService;
    private Gson gson = new Gson();

    private int[] orderStatusIds = {1, 2, 3, 4, 5}; // Mảng các ID trạng thái tương ứng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_update_order);

        initViews();
        saleApiService = ApiClient.getClient().create(SaleApiService.class);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderStatus.setAdapter(adapter);

        orderId = getIntent().getIntExtra("orderId", -1);

        if (orderId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng để chỉnh sửa.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fetchOrderDetails(orderId);

        btnAddProduct.setOnClickListener(v -> addOrderDetailField(null));
        btnUpdateOrder.setOnClickListener(v -> updateOrder());
    }

    private void initViews() {
        edtCustomerId = findViewById(R.id.edtCustomerId);
        edtPaymentMethodId = findViewById(R.id.edtPaymentMethodId);
        edtOrderNote = findViewById(R.id.edtOrderNote);
        spinnerOrderStatus = findViewById(R.id.spinnerOrderStatus);
        edtShippingAddress = findViewById(R.id.edtShippingAddress);
        orderDetailsContainer = findViewById(R.id.orderDetailsContainer);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnUpdateOrder = findViewById(R.id.btnUpdateOrder);
    }

    private void addOrderDetailField(SaleOrderDetailResponseDto detailData) {
        View detailView = LayoutInflater.from(this).inflate(R.layout.item_order_detail, orderDetailsContainer, false);

        EditText edtItemProductId = detailView.findViewById(R.id.edtItemProductId);
        EditText edtItemVariantId = detailView.findViewById(R.id.edtItemVariantId);
        EditText edtItemQuantity = detailView.findViewById(R.id.edtItemQuantity);
        EditText edtItemVariantAttributes = detailView.findViewById(R.id.edtItemVariantAttributes);
        Button btnRemoveItem = detailView.findViewById(R.id.btnRemoveItem);

        if (detailData != null) {
            if (detailData.getProductId() != null) edtItemProductId.setText(String.valueOf(detailData.getProductId()));
            if (detailData.getVariantId() != null) edtItemVariantId.setText(detailData.getVariantId());
            edtItemQuantity.setText(String.valueOf(detailData.getQuantity()));
            if (detailData.getVariantAttributes() != null && !detailData.getVariantAttributes().isEmpty()) {
                try {
                    Type type = new TypeToken<Map<String, String>>() {}.getType();
                    Map<String, String> attrs = gson.fromJson(detailData.getVariantAttributes(), type);
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> entry : attrs.entrySet()) {
                        if (sb.length() > 0) sb.append(",");
                        sb.append(entry.getKey()).append(":").append(entry.getValue());
                    }
                    edtItemVariantAttributes.setText(sb.toString());
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "Lỗi parse variantAttributes JSON string: " + e.getMessage());
                    edtItemVariantAttributes.setText(detailData.getVariantAttributes());
                }
            }
        }

        btnRemoveItem.setOnClickListener(v -> {
            if (orderDetailsContainer.getChildCount() > 1) {
                orderDetailsContainer.removeView(detailView);
            } else {
                Toast.makeText(SaleUpdateOrderActivity.this, "Đơn hàng phải có ít nhất một sản phẩm.", Toast.LENGTH_SHORT).show();
            }
        });

        orderDetailsContainer.addView(detailView);
    }


    private void fetchOrderDetails(int id) {
        Log.d(TAG, "1. fetchOrderDetails: Đang tải chi tiết đơn hàng với ID: " + id);
        saleApiService.getOrderById(id).enqueue(new Callback<SaleOrder>() {
            @Override
            public void onResponse(Call<SaleOrder> call, Response<SaleOrder> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SaleOrder saleOrder = response.body();
                    // Log toàn bộ JSON response để kiểm tra cấu trúc và giá trị
                    Log.d(TAG, "2. fetchOrderDetails: Đã tải thành công chi tiết đơn hàng: " + gson.toJson(saleOrder));
                    // Log riêng orderStatusId được tải về
                    Log.d(TAG, "3. fetchOrderDetails: OrderStatusId được tải về từ API: " + saleOrder.getOrderStatusId());

                    if (saleOrder.getCustomerId() != null) edtCustomerId.setText(String.valueOf(saleOrder.getCustomerId()));
                    if (saleOrder.getPaymentMethodId() != null) edtPaymentMethodId.setText(String.valueOf(saleOrder.getPaymentMethodId()));
                    if (saleOrder.getOrderNote() != null) edtOrderNote.setText(saleOrder.getOrderNote());

                    if (saleOrder.getOrderStatusId() != null) {
                        int statusId = saleOrder.getOrderStatusId();
                        boolean found = false;
                        for (int i = 0; i < orderStatusIds.length; i++) {
                            if (orderStatusIds[i] == statusId) {
                                spinnerOrderStatus.setSelection(i);
                                Log.d(TAG, "4. fetchOrderDetails: Đã đặt Spinner thành công tại vị trí: " + i + " (ID: " + statusId + ")");
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            Log.e(TAG, "5. fetchOrderDetails: Lỗi: OrderStatusId '" + statusId + "' tải về không tìm thấy trong mảng orderStatusIds đã định nghĩa!");
                            // Tùy chọn: đặt về mục đầu tiên hoặc một mục 'Không xác định' nếu có
                            spinnerOrderStatus.setSelection(0); // Đặt về trạng thái Pending mặc định
                        }
                    } else {
                        Log.d(TAG, "6. fetchOrderDetails: OrderStatusId tải về là NULL. Đặt Spinner về vị trí mặc định (Pending).");
                        spinnerOrderStatus.setSelection(0); // Đặt về trạng thái Pending mặc định
                    }

                    if (saleOrder.getShippingAddress() != null) edtShippingAddress.setText(saleOrder.getShippingAddress());

                    orderDetailsContainer.removeAllViews();

                    if (saleOrder.getOrderDetails() != null && !saleOrder.getOrderDetails().isEmpty()) {
                        for (SaleOrderDetailResponseDto detail : saleOrder.getOrderDetails()) {
                            addOrderDetailField(detail);
                        }
                    } else {
                        Log.d(TAG, "7. fetchOrderDetails: Đơn hàng không có chi tiết sản phẩm nào. Thêm một mục trống.");
                        Toast.makeText(SaleUpdateOrderActivity.this, "Đơn hàng này chưa có chi tiết sản phẩm. Vui lòng thêm.", Toast.LENGTH_SHORT).show();
                        addOrderDetailField(null);
                    }
                } else {
                    String errorMsg = "8. fetchOrderDetails: Lỗi khi tải chi tiết đơn hàng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + errorBodyString;
                            Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi server: " + errorBodyString, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "9. fetchOrderDetails: Error Body: " + errorBodyString);
                        } else {
                            Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi server không xác định: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "10. fetchOrderDetails: Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(SaleUpdateOrderActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "11. fetchOrderDetails: onResponse Error khi tải chi tiết: " + errorMsg);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SaleOrder> call, Throwable t) {
                Log.e(TAG, "12. fetchOrderDetails: Lỗi kết nối khi tải chi tiết đơn hàng: " + t.getMessage(), t);
                Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi kết nối server khi tải chi tiết đơn hàng.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void updateOrder() {
        String customerIdStr = edtCustomerId.getText().toString().trim();
        String paymentMethodIdStr = edtPaymentMethodId.getText().toString().trim();
        String orderNote = edtOrderNote.getText().toString().trim();

        // Lấy giá trị orderStatusId từ Spinner
        Integer orderStatusId = orderStatusIds[spinnerOrderStatus.getSelectedItemPosition()];
        Log.d(TAG, "13. updateOrder: OrderStatusId được chọn từ Spinner: " + orderStatusId +
                " (Vị trí: " + spinnerOrderStatus.getSelectedItemPosition() + ")");

        String shippingAddress = edtShippingAddress.getText().toString().trim();

        if (customerIdStr.isEmpty() || paymentMethodIdStr.isEmpty() || shippingAddress.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer customerId = null;
        try {
            customerId = Integer.parseInt(customerIdStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID Khách hàng không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer paymentMethodId = null;
        try {
            paymentMethodId = Integer.parseInt(paymentMethodIdStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID Phương thức thanh toán không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }


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

            if (productIdStr.isEmpty() && quantityStr.isEmpty() && variantId.isEmpty() && variantAttributesStr.isEmpty()) {
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
                if (!quantityStr.isEmpty() || !variantId.isEmpty() || !variantAttributesStr.isEmpty()) {
                    Toast.makeText(this, "ID sản phẩm không được để trống ở mục " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }
                continue;
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

        SaleUpdateOrderDto dto = new SaleUpdateOrderDto(
                customerId,
                paymentMethodId,
                orderStatusId,
                orderNote,
                shippingAddress,
                orderDetails
        );

        // Log toàn bộ DTO trước khi gửi lên API
        Log.d(TAG, "14. updateOrder: Đang gửi UpdateOrderDto cho OrderId " + orderId + ": " + gson.toJson(dto));

        saleApiService.updateOrder(orderId, dto).enqueue(new Callback<SaleOrderResponseDto>() {
            @Override
            public void onResponse(Call<SaleOrderResponseDto> call, Response<SaleOrderResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SaleUpdateOrderActivity.this, "Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                    // Log toàn bộ response body khi cập nhật thành công
                    Log.d(TAG, "15. updateOrder: Cập nhật thành công, Response Body: " + gson.toJson(response.body()));
                    // Log riêng orderStatusId từ response body
                    Log.d(TAG, "16. updateOrder: OrderStatusId từ Response Body: " + response.body().getOrderStatusId());
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = "17. updateOrder: Lỗi khi cập nhật đơn hàng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + errorBodyString;
                            Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi server: " + errorBodyString, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "18. updateOrder: Error Body: " + errorBodyString);
                        } else {
                            Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi server không xác định: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "19. updateOrder: Lỗi khi đọc errorBody khi cập nhật", e);
                        Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi server, không đọc được chi tiết lỗi.", Toast.LENGTH_LONG).show();
                    }
                    Log.e(TAG, "20. updateOrder: Lỗi API Update: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<SaleOrderResponseDto> call, Throwable t) {
                Log.e(TAG, "21. updateOrder: Lỗi kết nối khi cập nhật đơn hàng: " + t.getMessage(), t);
                Toast.makeText(SaleUpdateOrderActivity.this, "Không thể kết nối server", Toast.LENGTH_LONG).show();
            }
        });
    }
}