package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Dto.SaleOrderDetailRequestDto;
import com.example.login.login.shop_sqlite.Dto.SaleOrderDetailResponseDto;
import com.example.login.login.shop_sqlite.Dto.SaleUpdateOrderDto;
import com.example.login.login.shop_sqlite.Models.SaleOrder; // Model Order (ánh xạ OrderResponseDto từ backend)
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleUpdateOrderActivity extends AppCompatActivity {

    private static final String TAG = "UpdateOrderActivity";
    private EditText edtCustomerId, edtPaymentMethodId, edtOrderNote, edtOrderStatusId; // Thêm edtOrderStatusId
    private EditText edtProductId, edtVariantId, edtQuantity; // Các trường cho chi tiết sản phẩm đầu tiên
    private Button btnUpdateOrder;

    private int orderId;
    private SaleApiService saleApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_update_order);

        // Ánh xạ các thành phần UI
        edtCustomerId = findViewById(R.id.edtCustomerId);
        edtPaymentMethodId = findViewById(R.id.edtPaymentMethodId);
        edtOrderNote = findViewById(R.id.edtOrderNote);
        edtOrderStatusId = findViewById(R.id.edtOrderStatusId); // Ánh xạ trạng thái đơn hàng
        edtProductId = findViewById(R.id.edtProductId);
        edtVariantId = findViewById(R.id.edtVariantId);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnUpdateOrder = findViewById(R.id.btnUpdateOrder);

        saleApiService = ApiClient.getClient().create(SaleApiService.class);

        // Lấy OrderId từ Intent
        orderId = getIntent().getIntExtra("orderId", -1);

        if (orderId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng để chỉnh sửa.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Tải chi tiết đơn hàng hiện có để điền vào form
        fetchOrderDetails(orderId);

        // Thiết lập sự kiện click cho nút cập nhật
        btnUpdateOrder.setOnClickListener(v -> updateOrder());
    }

    /**
     * Tải chi tiết đơn hàng từ API và điền vào các trường EditText.
     * @param id OrderId của đơn hàng cần tải.
     */
    private void fetchOrderDetails(int id) {
        Log.d(TAG, "Đang tải chi tiết đơn hàng với ID: " + id);
        saleApiService.getOrderById(id).enqueue(new Callback<SaleOrder>() {
            @Override
            public void onResponse(Call<SaleOrder> call, Response<SaleOrder> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SaleOrder saleOrder = response.body();
                    Log.d(TAG, "Đã tải thành công chi tiết đơn hàng: " + saleOrder.getOrderId());

                    // Điền dữ liệu tổng quan đơn hàng
                    if (saleOrder.getCustomerId() != null) edtCustomerId.setText(String.valueOf(saleOrder.getCustomerId()));
                    if (saleOrder.getPaymentMethodId() != null) edtPaymentMethodId.setText(String.valueOf(saleOrder.getPaymentMethodId()));
                    if (saleOrder.getOrderNote() != null) edtOrderNote.setText(saleOrder.getOrderNote());
                    if (saleOrder.getOrderStatusId() != null) edtOrderStatusId.setText(String.valueOf(saleOrder.getOrderStatusId())); // Điền trạng thái

                    // Điền dữ liệu cho chi tiết sản phẩm đầu tiên (nếu có)
                    // Lưu ý: Nếu có nhiều sản phẩm, bạn sẽ cần một cơ chế phức tạp hơn (ví dụ: RecyclerView)
                    if (saleOrder.getOrderDetails() != null && !saleOrder.getOrderDetails().isEmpty()) {
                        SaleOrderDetailResponseDto firstDetail = saleOrder.getOrderDetails().get(0);
                        if (firstDetail.getProductId() != null) edtProductId.setText(String.valueOf(firstDetail.getProductId()));
                        if (firstDetail.getVariantId() != null) edtVariantId.setText(firstDetail.getVariantId());
                        edtQuantity.setText(String.valueOf(firstDetail.getQuantity()));
                    } else {
                        Log.d(TAG, "Đơn hàng không có chi tiết sản phẩm nào.");
                        Toast.makeText(SaleUpdateOrderActivity.this, "Đơn hàng này chưa có chi tiết sản phẩm.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi khi tải chi tiết đơn hàng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - Chi tiết: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(SaleUpdateOrderActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onResponse Error khi tải chi tiết: " + errorMsg);
                    finish(); // Đóng Activity nếu không tải được chi tiết
                }
            }

            @Override
            public void onFailure(Call<SaleOrder> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi tải chi tiết đơn hàng: " + t.getMessage(), t);
                Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi kết nối server khi tải chi tiết đơn hàng.", Toast.LENGTH_LONG).show();
                finish(); // Đóng Activity nếu lỗi kết nối
            }
        });
    }

    /**
     * Thu thập dữ liệu từ các trường và gửi yêu cầu cập nhật lên API.
     */
    private void updateOrder() {
        // Lấy dữ liệu từ các EditText
        String customerIdStr = edtCustomerId.getText().toString();
        String paymentMethodIdStr = edtPaymentMethodId.getText().toString();
        String orderNote = edtOrderNote.getText().toString();
        String orderStatusIdStr = edtOrderStatusId.getText().toString(); // Lấy trạng thái
        String productIdStr = edtProductId.getText().toString();
        String quantityStr = edtQuantity.getText().toString();
        String variantId = edtVariantId.getText().toString();

        // Kiểm tra validation cơ bản
        if (customerIdStr.isEmpty() || paymentMethodIdStr.isEmpty() ||
                productIdStr.isEmpty() || quantityStr.isEmpty() || orderStatusIdStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi sang kiểu dữ liệu phù hợp
        Integer customerId = Integer.parseInt(customerIdStr);
        Integer paymentMethodId = Integer.parseInt(paymentMethodIdStr);
        Integer orderStatusId = Integer.parseInt(orderStatusIdStr); // Chuyển đổi trạng thái

        Integer productId = null;
        if (!productIdStr.isEmpty()) { // ProductId có thể null trên backend, nhưng ở đây ta parse nó
            productId = Integer.parseInt(productIdStr);
        }

        int quantity = Integer.parseInt(quantityStr);

        if (quantity <= 0) {
            Toast.makeText(this, "Số lượng sản phẩm phải lớn hơn 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo danh sách OrderDetailRequestDto (hiện tại chỉ với một sản phẩm)
        List<SaleOrderDetailRequestDto> orderDetails = new ArrayList<>();
        orderDetails.add(new SaleOrderDetailRequestDto(productId, variantId.isEmpty() ? null : variantId, quantity));

        // Tạo UpdateOrderDto
        SaleUpdateOrderDto dto = new SaleUpdateOrderDto(customerId, paymentMethodId, orderStatusId, orderNote, orderDetails);

        // Gửi yêu cầu cập nhật lên API
        Log.d(TAG, "Đang gửi UpdateOrderDto cho OrderId " + orderId + ": " + new com.google.gson.Gson().toJson(dto));

        saleApiService.updateOrder(orderId, dto).enqueue(new Callback<SaleOrder>() {
            @Override
            public void onResponse(Call<SaleOrder> call, Response<SaleOrder> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SaleUpdateOrderActivity.this, "Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Đặt kết quả thành công để Activity gọi biết
                    finish(); // Đóng Activity
                } else {
                    String errorMsg = "Lỗi khi cập nhật đơn hàng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + errorBodyString;
                            Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi server: " + errorBodyString, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi server không xác định: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi đọc errorBody khi cập nhật", e);
                        Toast.makeText(SaleUpdateOrderActivity.this, "Lỗi server, không đọc được chi tiết lỗi.", Toast.LENGTH_LONG).show();
                    }
                    Log.e(TAG, "Lỗi API Update: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<SaleOrder> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi cập nhật đơn hàng: " + t.getMessage(), t);
                Toast.makeText(SaleUpdateOrderActivity.this, "Không thể kết nối server", Toast.LENGTH_LONG).show();
            }
        });
    }
}