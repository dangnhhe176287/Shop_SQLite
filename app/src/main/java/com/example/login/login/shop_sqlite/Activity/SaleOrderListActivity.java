package com.example.login.login.shop_sqlite.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Adapter.SaleOrderAdapter;
import com.example.login.login.shop_sqlite.Models.SaleOrder;
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleOrderListActivity extends AppCompatActivity implements SaleOrderAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private SaleOrderAdapter adapter;
    private List<SaleOrder> saleOrderList;
    private SaleApiService saleApiService;
    private Button btnCreateNewOrder;

    private static final int REQUEST_CODE_EDIT_ORDER = 1;
    private static final int REQUEST_CODE_CREATE_ORDER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_order_list);

        recyclerView = findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        saleOrderList = new ArrayList<>();
        adapter = new SaleOrderAdapter(saleOrderList, this);
        recyclerView.setAdapter(adapter);

        saleApiService = ApiClient.getClient().create(SaleApiService.class);

        btnCreateNewOrder = findViewById(R.id.btnCreateNewOrder);

        btnCreateNewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(SaleOrderListActivity.this, SaleCreateOrderActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_ORDER);
        });

        fetchOrders();
    }

    private void fetchOrders() {
        Log.d("OrderListActivity", "Bắt đầu tải danh sách đơn hàng...");
        saleApiService.getAllOrders().enqueue(new Callback<List<SaleOrder>>() {
            @Override
            public void onResponse(Call<List<SaleOrder>> call, Response<List<SaleOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saleOrderList.clear();
                    saleOrderList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d("OrderListActivity", "Đã tải " + saleOrderList.size() + " đơn hàng.");
                } else {
                    String errorBody = "Không có thông tin lỗi";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("OrderListActivity", "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(SaleOrderListActivity.this, "Lỗi khi tải đơn hàng: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e("OrderListActivity", "Lỗi tải đơn hàng: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<SaleOrder>> call, Throwable t) {
                Log.e("OrderListActivity", "Lỗi kết nối khi tải đơn hàng", t);
                Toast.makeText(SaleOrderListActivity.this, "Không thể kết nối đến máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(int orderId) {
        Log.d("OrderListActivity", "Chỉnh sửa đơn hàng với ID: " + orderId);
        Intent intent = new Intent(SaleOrderListActivity.this, SaleUpdateOrderActivity.class);
        intent.putExtra("orderId", orderId);
        startActivityForResult(intent, REQUEST_CODE_EDIT_ORDER);
    }

    @Override
    public void onDeleteClick(int orderId) {
        Log.d("OrderListActivity", "Yêu cầu xóa đơn hàng với ID: " + orderId);
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa đơn hàng này không?")
                .setPositiveButton("Xóa", (dialog, which) -> confirmDeleteOrder(orderId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDetailClick(int orderId) {
        Log.d("OrderListActivity", "Xem chi tiết đơn hàng với ID: " + orderId);
        Intent intent = new Intent(SaleOrderListActivity.this, SaleOrderDetailActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }


    private void confirmDeleteOrder(int orderId) {
        saleApiService.deleteOrder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SaleOrderListActivity.this, "Đã xóa đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                    fetchOrders();
                } else {
                    String errorMsg = "Lỗi khi xóa đơn hàng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + errorBodyString;
                            Toast.makeText(SaleOrderListActivity.this, "Lỗi server: " + errorBodyString, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("OrderListActivity", "Lỗi khi đọc errorBody khi xóa", e);
                    }
                    Log.e("OrderListActivity", errorMsg);
                    Toast.makeText(SaleOrderListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String networkError = "Không thể kết nối đến máy chủ khi xóa đơn hàng: " + t.getMessage();
                Log.e("OrderListActivity", networkError, t);
                Toast.makeText(SaleOrderListActivity.this, networkError, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_EDIT_ORDER || requestCode == REQUEST_CODE_CREATE_ORDER) && resultCode == RESULT_OK) {
            fetchOrders();
            Toast.makeText(this, "Danh sách đơn hàng đã được cập nhật.", Toast.LENGTH_SHORT).show();
        }
    }
}