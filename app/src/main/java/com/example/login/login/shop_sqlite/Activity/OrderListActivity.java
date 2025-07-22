package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Adapter.OrderListAdapter;
import com.example.login.login.shop_sqlite.Models.OrderView;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.R;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListActivity extends AppCompatActivity {
    private static final String TAG = "OrderListActivity";
    private RecyclerView recyclerView;
    private OrderListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Lấy userId từ SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("current_user_id", 0);
        
        Log.d(TAG, "Fetching orders for userId: " + userId);
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getOrders(userId).enqueue(new Callback<List<OrderView>>() {
            @Override
            public void onResponse(Call<List<OrderView>> call, Response<List<OrderView>> response) {
                Log.d(TAG, "API Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderView> orders = response.body();
                    Log.d(TAG, "Received " + orders.size() + " orders");
                    for (OrderView order : orders) {
                        Log.d(TAG, "Order ID: " + order.getOrderId() + ", Amount: " + order.getAmountDue());
                    }
                    adapter = new OrderListAdapter(orders);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Toast.makeText(OrderListActivity.this, "Không lấy được đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<OrderView>> call, Throwable t) {
                Log.e(TAG, "API Call Failed", t);
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 