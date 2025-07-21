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
import com.example.login.login.shop_sqlite.Adapter.ProductAdapter;
import com.example.login.login.shop_sqlite.Dto.ProductResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListFragment extends Fragment implements ProductAdapter.OnItemActionListener {

    private static final String TAG = "ProductListFragment";

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<ProductResponseDto> productList;
    private ApiService apiService;
    private Button btnCreateNewProduct;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnCreateNewProduct = view.findViewById(R.id.btnCreateNewProduct);
        btnCreateNewProduct.setOnClickListener(v -> {
            // Logic để thêm sản phẩm mới
            Toast.makeText(getContext(), "Chức năng thêm sản phẩm mới sẽ được triển khai sau!", Toast.LENGTH_SHORT).show();
        });

        fetchProducts();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchProducts();
    }

    private void fetchProducts() {
        Log.d(TAG, "Đang tải danh sách sản phẩm...");
        apiService.getAllProducts().enqueue(new Callback<List<ProductResponseDto>>() {
            @Override
            public void onResponse(Call<List<ProductResponseDto>> call, Response<List<ProductResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Đã tải " + productList.size() + " sản phẩm.");
                } else {
                    String errorBody = "Không có thông tin lỗi";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(getContext(), "Lỗi khi tải sản phẩm: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi tải sản phẩm: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponseDto>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi tải sản phẩm", t);
                Toast.makeText(getContext(), "Không thể kết nối đến máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(int productId) {
        Toast.makeText(getContext(), "Chức năng chỉnh sửa sản phẩm ID: " + productId + " sẽ được triển khai.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int productId) {
        Toast.makeText(getContext(), "Chức năng xóa sản phẩm ID: " + productId + " sẽ được triển khai.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}