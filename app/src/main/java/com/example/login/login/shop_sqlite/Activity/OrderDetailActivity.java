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
                    Toast.makeText(OrderDetailActivity.this, "Không lấy được thông tin đơn hàng", Toast.LENGTH_SHORT).show();
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
        try {
            // Parse and format date
            Date date = inputFormat.parse(order.getCreatedAt());
            String formattedDate = outputFormat.format(date);
            
            // Display order information
            tvOrderId.setText("Đơn hàng #" + order.getOrderId());
            tvTotal.setText(formatPrice(order.getAmountDue()));
            tvDate.setText(formattedDate);
            tvAddress.setText(order.getShippingAddress());
            tvNote.setText(order.getOrderNote() != null ? order.getOrderNote() : "Không có ghi chú");
            tvOrderStatus.setText(order.getOrderStatusTitle());
            tvOrderStatus.setBackgroundColor(getStatusColor(order.getOrderStatusId()));
            
            // Setup RecyclerView for order items
            adapter = new OrderDetailAdapter(order.getItems());
            recyclerView.setAdapter(adapter);
            
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date", e);
            tvDate.setText(order.getCreatedAt()); // Fallback to original string
        }
    }
    
    private String formatPrice(double price) {
        return String.format(Locale.getDefault(), "%,.0f VNĐ", price);
    }
    
    private int getStatusColor(int statusId) {
        switch (statusId) {
            case 1: return getResources().getColor(android.R.color.holo_blue_dark); // Pending
            case 2: return getResources().getColor(android.R.color.holo_orange_dark); // Processing
            case 3: return getResources().getColor(android.R.color.holo_green_dark); // Shipped
            case 4: return getResources().getColor(android.R.color.holo_green_light); // Delivered
            case 5: return getResources().getColor(android.R.color.holo_red_dark); // Cancelled
            default: return getResources().getColor(android.R.color.darker_gray);
        }
    }
}
