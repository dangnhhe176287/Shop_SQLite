package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Adapter.OrderDetailAdapter;
import com.example.login.login.shop_sqlite.Models.OrderView;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailActivity";
    private TextView tvOrderId, tvTotal, tvDate, tvAddress, tvNote, tvOrderStatus;
    private RecyclerView recyclerView;
    private OrderDetailAdapter adapter;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat outputFormat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Initialize date formats
        inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault());
        outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        // Initialize views
        tvOrderId = findViewById(R.id.tvOrderId);
        tvTotal = findViewById(R.id.tvTotal);
        tvDate = findViewById(R.id.tvDate);
        tvAddress = findViewById(R.id.tvAddress);
        tvNote = findViewById(R.id.tvNote);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        recyclerView = findViewById(R.id.recyclerViewOrderDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Get order ID from intent
        int orderId = getIntent().getIntExtra("order_id", 0);
        if (orderId == 0) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Fetch order details
        fetchOrderDetail(orderId);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void fetchOrderDetail(int orderId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getOrderDetail(orderId).enqueue(new Callback<OrderView>() {
            @Override
            public void onResponse(Call<OrderView> call, Response<OrderView> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderView order = response.body();
                    displayOrderDetail(order);
                } else {
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    Toast.makeText(OrderDetailActivity.this, "Không lấy được chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<OrderView> call, Throwable t) {
                Log.e(TAG, "API Call Failed", t);
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayOrderDetail(OrderView order) {
        // Set order information
        tvOrderId.setText("Mã đơn hàng: #" + order.getOrderId());
        tvTotal.setText("Tổng tiền: " + formatPrice(order.getAmountDue()));
        
        // Hiển thị trạng thái đơn hàng
        String statusText = order.getOrderStatusTitle() != null ? order.getOrderStatusTitle() : "Đang xử lý";
        tvOrderStatus.setText(statusText);
        
        // Đặt màu cho trạng thái
        int statusColor = getStatusColor(order.getOrderStatusId());
        tvOrderStatus.setBackgroundColor(statusColor);
        
        // Format date
        String dateText = "Ngày đặt: ";
        if (order.getCreatedAt() != null && !order.getCreatedAt().isEmpty()) {
            try {
                Date date = inputFormat.parse(order.getCreatedAt());
                dateText += outputFormat.format(date);
            } catch (ParseException e) {
                dateText += order.getCreatedAt();
            }
        } else {
            dateText += "N/A";
        }
        tvDate.setText(dateText);
        
        // Set address and note
        tvAddress.setText("Địa chỉ giao hàng: " + (order.getShippingAddress() != null ? order.getShippingAddress() : "N/A"));
        tvNote.setText("Ghi chú: " + (order.getOrderNote() != null ? order.getOrderNote() : "Không có"));
        
        // Debug order items
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            Log.d(TAG, "Order has " + order.getItems().size() + " items");
            for (OrderView.OrderDetailView item : order.getItems()) {
                Log.d(TAG, "Item: " + item.getProductName() + ", Variant: " + item.getVariantAttributes());
            }
            adapter = new OrderDetailAdapter(order.getItems());
            recyclerView.setAdapter(adapter);
        } else {
            Log.d(TAG, "Order has no items");
        }
    }
    
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
    
    private int getStatusColor(int statusId) {
        switch (statusId) {
            case 1: // Pending
                return 0xFFE91E63; // Pink
            case 2: // Processing
                return 0xFFFF9800; // Orange
            case 3: // Shipped
                return 0xFF2196F3; // Blue
            case 4: // Delivered
                return 0xFF4CAF50; // Green
            case 5: // Cancelled
                return 0xFFF44336; // Red
            default:
                return 0xFFE91E63; // Default Pink
        }
    }
} 