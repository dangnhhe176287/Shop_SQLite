package com.example.login.login.shop_sqlite.Activity;

<<<<<<< HEAD
import android.os.Bundle;
import android.widget.Toast;
=======

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
>>>>>>> hmthmt
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
import com.example.login.login.shop_sqlite.Adapter.OrderListAdapter;
import com.example.login.login.shop_sqlite.Models.OrderView;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.R;
import java.util.List;
=======
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Adapter.OrderAdapter; // Sẽ tạo OrderAdapter
import com.example.login.login.shop_sqlite.Models.Order; // Model Order (ánh xạ OrderResponseDto)
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

>>>>>>> hmthmt
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

<<<<<<< HEAD
public class OrderListActivity extends AppCompatActivity {
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
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getOrders(userId).enqueue(new Callback<List<OrderView>>() {
            @Override
            public void onResponse(Call<List<OrderView>> call, Response<List<OrderView>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new OrderListAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(OrderListActivity.this, "Không lấy được đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<OrderView>> call, Throwable t) {
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 
=======
public class OrderListActivity extends AppCompatActivity implements OrderAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private ApiService apiService;
    private Button btnCreateNewOrder;

    private static final int REQUEST_CODE_EDIT_ORDER = 1;
    private static final int REQUEST_CODE_CREATE_ORDER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list); // Sẽ tạo layout này

        recyclerView = findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList, this); // 'this' để truyền listener
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnCreateNewOrder = findViewById(R.id.btnCreateNewOrder); // Sẽ tạo button này trong layout

        btnCreateNewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderListActivity.this, CreateOrderActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_ORDER);
        });

        fetchOrders();
    }

    private void fetchOrders() {
        Log.d("OrderListActivity", "Bắt đầu tải danh sách đơn hàng...");
        apiService.getAllOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d("OrderListActivity", "Đã tải " + orderList.size() + " đơn hàng.");
                } else {
                    String errorBody = "Không có thông tin lỗi";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("OrderListActivity", "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(OrderListActivity.this, "Lỗi khi tải đơn hàng: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e("OrderListActivity", "Lỗi tải đơn hàng: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("OrderListActivity", "Lỗi kết nối khi tải đơn hàng", t);
                Toast.makeText(OrderListActivity.this, "Không thể kết nối đến máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(int orderId) {
        Log.d("OrderListActivity", "Chỉnh sửa đơn hàng với ID: " + orderId);
        Intent intent = new Intent(OrderListActivity.this, UpdateOrderActivity.class);
        intent.putExtra("orderId", orderId);
        startActivityForResult(intent, REQUEST_CODE_EDIT_ORDER);
    }

    @Override
    public void onDeleteClick(int orderId) {
        Log.d("OrderListActivity", "Yêu cầu xóa đơn hàng với ID: " + orderId);
        apiService.deleteOrder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderListActivity.this, "Đã xóa đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                    fetchOrders();
                } else {
                    String errorMsg = "Lỗi khi xóa đơn hàng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + errorBodyString;
                            Toast.makeText(OrderListActivity.this, "Lỗi server: " + errorBodyString, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("OrderListActivity", "Lỗi khi đọc errorBody khi xóa", e);
                    }
                    Log.e("OrderListActivity", errorMsg);
                    Toast.makeText(OrderListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String networkError = "Không thể kết nối đến máy chủ khi xóa đơn hàng: " + t.getMessage();
                Log.e("OrderListActivity", networkError, t);
                Toast.makeText(OrderListActivity.this, networkError, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_EDIT_ORDER || requestCode == REQUEST_CODE_CREATE_ORDER) && resultCode == RESULT_OK) {
            fetchOrders(); // Tải lại danh sách sau khi tạo hoặc cập nhật thành công
            Toast.makeText(this, "Danh sách đơn hàng đã được cập nhật.", Toast.LENGTH_SHORT).show();
        }
    }
}
>>>>>>> hmthmt
