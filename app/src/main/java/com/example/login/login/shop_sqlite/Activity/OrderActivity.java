package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Adapter.OrderAdapter;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Models.Order;
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private ListView orderListView;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderListView = findViewById(R.id.orderListView);
        apiService = ApiClient.getClient().create(ApiService.class);

        loadOrdersFromApi();
    }

    private void loadOrdersFromApi() {
        Call<List<Order>> call = apiService.getAllOrders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();
                    OrderAdapter adapter = new OrderAdapter(OrderActivity.this, orders);
                    orderListView.setAdapter(adapter);
                } else {
                    Toast.makeText(OrderActivity.this, "Lỗi lấy dữ liệu đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Lỗi kết nối tới server: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}