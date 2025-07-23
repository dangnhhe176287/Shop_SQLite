package com.example.login.login.shop_sqlite.SaleFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import com.example.login.login.shop_sqlite.Activity.SaleCreateOrderActivity;
import com.example.login.login.shop_sqlite.Activity.SaleUpdateOrderActivity;
import com.example.login.login.shop_sqlite.Activity.SaleOrderDetailActivity; // Import OrderDetailActivity
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

public class OrderListFragment extends Fragment implements SaleOrderAdapter.OnItemActionListener {

    private static final String TAG = "OrderListFragment";
    private static final int REQUEST_CODE_CREATE_ORDER = 1;
    private static final int REQUEST_CODE_UPDATE_ORDER = 2;

    private RecyclerView recyclerOrders;
    private SaleOrderAdapter adapter;
    private List<SaleOrder> saleOrderList;
    private SaleApiService saleApiService;
    private Button btnCreateNewOrder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sale_fragment_order_list, container, false);

        recyclerOrders = view.findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        saleOrderList = new ArrayList<>();
        adapter = new SaleOrderAdapter(saleOrderList, this); // 'this' để truyền listener vào adapter
        recyclerOrders.setAdapter(adapter);

        saleApiService = ApiClient.getClient().create(SaleApiService.class);

        btnCreateNewOrder = view.findViewById(R.id.btnCreateNewOrder);
        btnCreateNewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SaleCreateOrderActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_ORDER);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchOrders();
    }

    private void refreshOrderList() {
        fetchOrders();
    }

    private void fetchOrders() {
        Log.d(TAG, "Đang tải danh sách đơn hàng...");
        saleApiService.getAllOrders().enqueue(new Callback<List<SaleOrder>>() {
            @Override
            public void onResponse(Call<List<SaleOrder>> call, Response<List<SaleOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saleOrderList.clear();
                    saleOrderList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Đã tải " + saleOrderList.size() + " đơn hàng.");
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
            public void onFailure(Call<List<SaleOrder>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi tải đơn hàng", t);
                Toast.makeText(getContext(), "Không thể kết nối đến máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(int orderId) {
        Log.d(TAG, "Đã nhấp chỉnh sửa đơn hàng ID: " + orderId);
        Intent intent = new Intent(getContext(), SaleUpdateOrderActivity.class);
        intent.putExtra("orderId", orderId);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_ORDER);
    }

    @Override
    public void onDeleteClick(int orderId) {
        Log.d(TAG, "Đã nhấp xóa đơn hàng ID: " + orderId);
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa đơn hàng")
                .setMessage("Bạn có chắc chắn muốn xóa đơn hàng này không?")
                .setPositiveButton("Xóa", (dialog, which) -> confirmDeleteOrder(orderId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDetailClick(int orderId) { // Implementation for the new detail click
        Log.d(TAG, "Đã nhấp xem chi tiết đơn hàng ID: " + orderId);
        Intent intent = new Intent(getContext(), SaleOrderDetailActivity.class);
        intent.putExtra("orderId", orderId); // Pass the order ID
        startActivity(intent); // No need for startActivityForResult as no result is expected back
    }

    private void confirmDeleteOrder(int orderId) {
        saleApiService.deleteOrder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                    refreshOrderList();
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
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CODE_CREATE_ORDER || requestCode == REQUEST_CODE_UPDATE_ORDER) {
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