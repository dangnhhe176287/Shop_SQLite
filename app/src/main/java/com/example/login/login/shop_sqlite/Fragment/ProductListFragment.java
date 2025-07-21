package com.example.login.login.shop_sqlite.Fragment;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Activity.CreateProductActivity;
import com.example.login.login.shop_sqlite.Activity.EditProductActivity;
import com.example.login.login.shop_sqlite.Adapter.ProductAdapter;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.ProductResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListFragment extends Fragment implements ProductAdapter.OnItemActionListener {

    private static final String TAG = "ProductListFragment";
    private static final int REQUEST_CODE_CREATE_PRODUCT = 1;
    private static final int REQUEST_CODE_EDIT_PRODUCT = 2;

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
            Intent intent = new Intent(getActivity(), CreateProductActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_PRODUCT);
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
                    showError(response);
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponseDto>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi tải sản phẩm", t);
                Toast.makeText(getContext(), "Không thể kết nối đến máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showError(Response<?> response) {
        String errorMsg = "Lỗi: " + response.code();
        try {
            if (response.errorBody() != null) {
                String rawErrorBody = response.errorBody().string();
                errorMsg += " - Chi tiết: " + rawErrorBody;
                Log.e(TAG, "Lỗi phản hồi API (" + response.code() + "): " + rawErrorBody);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi đọc errorBody: " + e.getMessage(), e);
            errorMsg += " - Không thể đọc chi tiết lỗi.";
        }
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEditClick(int productId) {
        Intent intent = new Intent(getActivity(), EditProductActivity.class);
        intent.putExtra("productId", productId);
        startActivityForResult(intent, REQUEST_CODE_EDIT_PRODUCT);
    }

    @Override
    public void onDeleteClick(int productId) {
        apiService.deleteProduct(productId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    fetchProducts();
                } else {
                    showError(response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ khi xóa sản phẩm:", t);
                Toast.makeText(getContext(), "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_PRODUCT && resultCode == AppCompatActivity.RESULT_OK) {
            fetchProducts();
        } else if (requestCode == REQUEST_CODE_EDIT_PRODUCT && resultCode == AppCompatActivity.RESULT_OK) {
            fetchProducts();
        }
    }
}