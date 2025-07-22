package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Adapter.OrderDetailItemAdapter;
import com.example.login.login.shop_sqlite.Models.Order;
import com.example.login.login.shop_sqlite.R;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderId, tvCustomerId, tvTotalQuantity, tvAmountDue, tvPaymentMethod, tvOrderNote, tvOrderStatus;
    private RecyclerView recyclerOrderDetails;
    private OrderDetailItemAdapter detailAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        tvOrderId = findViewById(R.id.tvOrderDetailId);
        tvCustomerId = findViewById(R.id.tvOrderDetailCustomerId);
        tvTotalQuantity = findViewById(R.id.tvOrderDetailTotalQuantity);
        tvAmountDue = findViewById(R.id.tvOrderDetailAmountDue);
        tvPaymentMethod = findViewById(R.id.tvOrderDetailPaymentMethod);
        tvOrderNote = findViewById(R.id.tvOrderDetailNote);
        tvOrderStatus = findViewById(R.id.tvOrderDetailStatus);
        recyclerOrderDetails = findViewById(R.id.recyclerOrderDetailItems);

        recyclerOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        apiService = ApiClient.getClient().create(ApiService.class);

        int orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId != -1) {
            fetchOrderDetail(orderId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchOrderDetail(int id) {
        apiService.getOrderById(id).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();
                    tvOrderId.setText("Mã đơn: " + order.getOrderId());
                    tvCustomerId.setText("ID Khách hàng: " + (order.getCustomerId() != null ? order.getCustomerId() : "N/A"));
                    tvTotalQuantity.setText("Tổng số lượng: " + (order.getTotalQuantity() != null ? order.getTotalQuantity() : 0));
                    tvAmountDue.setText(String.format(Locale.getDefault(), "Tổng tiền: %.2f VNĐ", (order.getAmountDue() != null ? order.getAmountDue() : 0.0)));
                    tvPaymentMethod.setText("Phương thức TT: " + (order.getPaymentMethodId() != null ? order.getPaymentMethodId() : "N/A")); // Cần ánh xạ PaymentMethodId sang tên
                    tvOrderNote.setText("Ghi chú: " + (order.getOrderNote() != null ? order.getOrderNote() : "Không có"));
                    tvOrderStatus.setText("Trạng thái: " + (order.getOrderStatusId() != null ? getStatusName(order.getOrderStatusId()) : "N/A"));

                    detailAdapter = new OrderDetailItemAdapter(order.getOrderDetails());
                    recyclerOrderDetails.setAdapter(detailAdapter);

                } else {
                    String errorBody = "Không có thông tin lỗi";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("OrderDetail", "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(OrderDetailActivity.this, "Lỗi khi tải chi tiết đơn hàng: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e("OrderDetail", "Lỗi tải chi tiết đơn hàng: " + response.code() + " - " + errorBody);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e("OrderDetail", "Lỗi kết nối khi tải chi tiết đơn hàng", t);
                Toast.makeText(OrderDetailActivity.this, "Không thể kết nối đến máy chủ.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    // Hàm giả định để chuyển đổi OrderStatusId thành tên trạng thái dễ đọc
    private String getStatusName(int statusId) {
        switch (statusId) {
            case 1: return "Chờ xử lý";
            case 2: return "Đang xử lý";
            case 3: return "Đã giao hàng";
            case 4: return "Đã hủy";
            default: return "Không xác định";
        }
    }
}