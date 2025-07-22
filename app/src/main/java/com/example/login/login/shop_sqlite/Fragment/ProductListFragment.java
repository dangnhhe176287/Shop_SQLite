// com.example.login.login.shop_sqlite.Fragment.ProductListFragment.java
// (Hoặc Activity tương ứng của bạn)

package com.example.login.login.shop_sqlite.Fragment;

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

import com.example.login.login.shop_sqlite.Activity.CreateProductActivity;
import com.example.login.login.shop_sqlite.Activity.EditProductActivity;
import com.example.login.login.shop_sqlite.Activity.ProductDetailActivity; // <--- Import ProductDetailActivity
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

// Đảm bảo ProductListFragment implement ProductAdapter.OnItemActionListener
public class ProductListFragment extends Fragment implements ProductAdapter.OnItemActionListener {

    private static final String TAG = "ProductListFragment";
    private static final int REQUEST_CODE_CREATE_PRODUCT = 101;
    private static final int REQUEST_CODE_UPDATE_PRODUCT = 102;

    private RecyclerView recyclerProducts;
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
        // Đảm bảo fragment_product_list.xml chứa RecyclerView và Button
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        recyclerProducts = view.findViewById(R.id.recyclerProducts);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, this); // 'this' để truyền listener
        recyclerProducts.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnCreateNewProduct = view.findViewById(R.id.btnCreateNewProduct);
        btnCreateNewProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateProductActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_PRODUCT);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchProducts();
    }

    private void refreshProductList() {
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
        Log.d(TAG, "Đã nhấp chỉnh sửa sản phẩm ID: " + productId);
        Intent intent = new Intent(getContext(), EditProductActivity.class);
        intent.putExtra("productId", productId);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PRODUCT);
    }

    @Override
    public void onDeleteClick(int productId) {
        Log.d(TAG, "Đã nhấp xóa sản phẩm ID: " + productId);
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                .setPositiveButton("Xóa", (dialog, which) -> confirmDeleteProduct(productId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDetailClick(int productId) { // <--- TRIỂN KHAI PHƯƠNG THỨC MỚI
        Log.d(TAG, "Đã nhấp xem chi tiết sản phẩm ID: " + productId);
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra("productId", productId); // Truyền ID sản phẩm
        startActivity(intent); // Chỉ cần startActivity vì không cần kết quả trả về
    }
    // TRIỂN KHAI PHƯƠNG THỨC MỚI --->

    private void confirmDeleteProduct(int productId) {
        apiService.deleteProduct(productId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    refreshProductList();
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Không tìm thấy sản phẩm để xóa.", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Lỗi khi xóa sản phẩm: " + response.code();
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
            if (requestCode == REQUEST_CODE_CREATE_PRODUCT || requestCode == REQUEST_CODE_UPDATE_PRODUCT) {
                refreshProductList();
                Toast.makeText(getContext(), "Danh sách sản phẩm đã được cập nhật.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}