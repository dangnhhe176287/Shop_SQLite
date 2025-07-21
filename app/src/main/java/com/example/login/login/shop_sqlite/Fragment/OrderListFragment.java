package com.example.login.login.shop_sqlite.Fragment;

import android.content.Context;
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

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Adapter.OrderAdapter;
import com.example.login.login.shop_sqlite.Models.Order;
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListFragment extends Fragment implements OrderAdapter.OnItemActionListener {

    private static final String TAG = "OrderListFragment";

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
        adapter = new OrderAdapter(orderList, this);
        recyclerOrders.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnCreateNewOrder = view.findViewById(R.id.btnCreateNewOrder); // Nếu cần thêm nút tạo mới
        btnCreateNewOrder.setOnClickListener(v -> {
            // Logic để thêm đơn hàng mới
            Toast.makeText(getContext(), "Chức năng thêm đơn hàng mới sẽ được triển khai sau!", Toast.LENGTH_SHORT).show();
        });

        fetchOrders();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        // Logic để chỉnh sửa đơn hàng
        Toast.makeText(getContext(), "Chức năng chỉnh sửa đơn hàng ID: " + orderId + " sẽ được triển khai.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int orderId) {
        // Logic để xóa đơn hàng
        Toast.makeText(getContext(), "Chức năng xóa đơn hàng ID: " + orderId + " sẽ được triển khai.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}