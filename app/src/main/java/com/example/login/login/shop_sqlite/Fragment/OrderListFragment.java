package com.example.login.login.shop_sqlite.Fragment;

import android.app.AlertDialog; // Import AlertDialog
import android.content.Context;
import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Activity.CreateOrderActivity; // Import CreateOrderActivity
import com.example.login.login.shop_sqlite.Activity.UpdateOrderActivity; // Import UpdateOrderActivity
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Adapter.OrderAdapter;
import com.example.login.login.shop_sqlite.Models.Order; // Đảm bảo Order model là đúng DTO cho phản hồi API
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListFragment extends Fragment implements OrderAdapter.OnItemActionListener {

    private static final String TAG = "OrderListFragment";
    private static final int REQUEST_CODE_CREATE_ORDER = 1;
    private static final int REQUEST_CODE_UPDATE_ORDER = 2;

    private RecyclerView recyclerOrders;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private ApiService apiService;
    private Button btnCreateNewOrder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerOrders = view.findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList, this); // 'this' để truyền listener vào adapter
        recyclerOrders.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnCreateNewOrder = view.findViewById(R.id.btnCreateNewOrder);
        btnCreateNewOrder.setOnClickListener(v -> {
            // Mở CreateOrderActivity khi nhấn nút
            Intent intent = new Intent(getContext(), CreateOrderActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_ORDER);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchOrders(); // Tải danh sách đơn hàng khi Fragment được tạo View
    }

    // Phương thức để làm mới danh sách đơn hàng
    private void refreshOrderList() {
        fetchOrders();
    }

    private void fetchOrders() {
        Log.d(TAG, "Đang tải danh sách đơn hàng...");
        apiService.getAllOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Đã tải " + orderList.size() + " đơn hàng.");
                } else {
                    String errorBody = "Không có thông tin lỗi";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(getContext(), "Lỗi khi tải đơn hàng: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi tải đơn hàng: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi tải đơn hàng", t);
                Toast.makeText(getContext(), "Không thể kết nối đến máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(int orderId) {
        Log.d(TAG, "Đã nhấp chỉnh sửa đơn hàng ID: " + orderId);
        // Mở UpdateOrderActivity khi nhấn nút chỉnh sửa
        Intent intent = new Intent(getContext(), UpdateOrderActivity.class);
        intent.putExtra("orderId", orderId); // Truyền ID đơn hàng cần chỉnh sửa
        startActivityForResult(intent, REQUEST_CODE_UPDATE_ORDER); // Bắt đầu Activity để lấy kết quả
    }

    @Override
    public void onDeleteClick(int orderId) {
        Log.d(TAG, "Đã nhấp xóa đơn hàng ID: " + orderId);
        // Hiển thị hộp thoại xác nhận trước khi xóa
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa đơn hàng")
                .setMessage("Bạn có chắc chắn muốn xóa đơn hàng này không?")
                .setPositiveButton("Xóa", (dialog, which) -> confirmDeleteOrder(orderId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmDeleteOrder(int orderId) {
        apiService.deleteOrder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                    refreshOrderList(); // Làm mới danh sách sau khi xóa thành công
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Không tìm thấy đơn hàng để xóa.", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Lỗi khi xóa đơn hàng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String rawErrorBody = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + rawErrorBody;
                            Log.e(TAG, "Lỗi phản hồi API khi xóa (" + response.code() + "): " + rawErrorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody khi xóa: " + e.getMessage(), e);
                        errorMsg += " - Không thể đọc chi tiết lỗi.";
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ khi xóa:", t);
                Toast.makeText(getContext(), "Không thể kết nối máy chủ để xóa: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) { // Kiểm tra RESULT_OK từ Activity đã đóng
            if (requestCode == REQUEST_CODE_CREATE_ORDER || requestCode == REQUEST_CODE_UPDATE_ORDER) {
                // Nếu là kết quả từ tạo hoặc cập nhật, làm mới danh sách đơn hàng
                refreshOrderList();
                Toast.makeText(getContext(), "Danh sách đơn hàng đã được cập nhật.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}