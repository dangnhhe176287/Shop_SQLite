package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Adapter.SaleOrderDetailItemAdapter;
import com.example.login.login.shop_sqlite.Models.SaleOrder;
import com.example.login.login.shop_sqlite.R;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleOrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "SaleOrderDetailActivity";
    private TextView tvOrderId, tvCustomerId, tvTotalQuantity, tvAmountDue, tvPaymentMethod, tvOrderNote, tvOrderStatus;
    private RecyclerView recyclerOrderDetails;
    private SaleOrderDetailItemAdapter detailAdapter;
    private SaleApiService saleApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_order_detail);

        tvOrderId = findViewById(R.id.tvOrderDetailId);
        tvCustomerId = findViewById(R.id.tvOrderDetailCustomerId);
        tvTotalQuantity = findViewById(R.id.tvOrderDetailTotalQuantity);
        tvAmountDue = findViewById(R.id.tvOrderDetailAmountDue);
        tvPaymentMethod = findViewById(R.id.tvOrderDetailPaymentMethod);
        tvOrderNote = findViewById(R.id.tvOrderDetailNote);
        tvOrderStatus = findViewById(R.id.tvOrderDetailStatus);
        recyclerOrderDetails = findViewById(R.id.recyclerOrderDetailItems);

        recyclerOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        saleApiService = ApiClient.getClient().create(SaleApiService.class);

        int orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId != -1) {
            fetchOrderDetail(orderId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Lỗi: Không tìm thấy ID đơn hàng trong Intent.");
            finish();
        }
    }

    private void fetchOrderDetail(int id) {
        Log.d(TAG, "Đang tải chi tiết đơn hàng với ID: " + id);
        saleApiService.getOrderById(id).enqueue(new Callback<SaleOrder>() {
            @Override
            public void onResponse(Call<SaleOrder> call, Response<SaleOrder> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SaleOrder saleOrder = response.body();
                    Log.d(TAG, "Đã tải thành công chi tiết đơn hàng: " + saleOrder.getOrderId());

                    tvOrderId.setText("Mã đơn: " + saleOrder.getOrderId());
                    tvCustomerId.setText("ID Khách hàng: " + (saleOrder.getCustomerId() != null ? saleOrder.getCustomerId() : "N/A"));
                    tvTotalQuantity.setText("Tổng số lượng: " + (saleOrder.getTotalQuantity() != null ? saleOrder.getTotalQuantity() : 0));
                    tvAmountDue.setText(String.format(Locale.getDefault(), "Tổng tiền: %.2f VNĐ", (saleOrder.getAmountDue() != null ? saleOrder.getAmountDue() : 0.0)));
                    tvPaymentMethod.setText("Phương thức TT: " + (saleOrder.getPaymentMethodId() != null ? saleOrder.getPaymentMethodId() : "N/A")); // Cần ánh xạ PaymentMethodId sang tên

                    tvOrderNote.setText("Ghi chú: " + (saleOrder.getOrderNote() != null ? saleOrder.getOrderNote() : "Không có"));

                    Integer orderStatusId = saleOrder.getOrderStatusId();
                    Log.d(TAG, "OrderStatusId từ API khi xem chi tiết: " + orderStatusId);
                    tvOrderStatus.setText("Trạng thái: " + (orderStatusId != null ? getStatusName(orderStatusId) : "N/A"));

                    detailAdapter = new SaleOrderDetailItemAdapter(saleOrder.getOrderDetails());
                    recyclerOrderDetails.setAdapter(detailAdapter);

                } else {
                    String errorBody = "Không có thông tin lỗi";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(SaleOrderDetailActivity.this, "Lỗi khi tải chi tiết đơn hàng: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi tải chi tiết đơn hàng: " + response.code() + " - " + errorBody);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SaleOrder> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi tải chi tiết đơn hàng", t);
                Toast.makeText(SaleOrderDetailActivity.this, "Không thể kết nối đến máy chủ.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private String getStatusName(int statusId) {
        switch (statusId) {
            case 1: return "Pending";
            case 2: return "Shipping";
            case 3: return "Delivered";
            case 4: return "Completed";
            case 5: return "Canceled";
            default: return "Không xác định (" + statusId + ")";
        }
    }
}