package com.example.login.login.shop_sqlite.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.login.login.shop_sqlite.R;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Adapter.CategoryAdapter;
import com.example.login.login.shop_sqlite.Dto.ProductCategoryResponseDto;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryActionListener {

    private static final String TAG = "ProductCategoriesFrag";

    private TextView txtHeader;
    private Button btnAddCategory;
    private ListView listViewCategories;
    private ApiService apiService;
    private List<ProductCategoryResponseDto> categoryList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_product_categories, container, false);

        txtHeader = view.findViewById(R.id.txtHeader);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        listViewCategories = view.findViewById(R.id.listViewCategories);

        txtHeader.setText("Danh mục Sản phẩm");

        apiService = ApiClient.getClient().create(ApiService.class);

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, this);
        listViewCategories.setAdapter(categoryAdapter);

        btnAddCategory.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng thêm danh mục mới sẽ được triển khai sau!", Toast.LENGTH_SHORT).show();

        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchProductCategories();
    }

    public void refreshProductCategoryList() {
        fetchProductCategories();
    }

    private void fetchProductCategories() {
        Log.d(TAG, "Đang tải danh sách danh mục sản phẩm...");
        apiService.getAllProductCategories().enqueue(new Callback<List<ProductCategoryResponseDto>>() {
            @Override
            public void onResponse(Call<List<ProductCategoryResponseDto>> call, Response<List<ProductCategoryResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();

                    if (categoryList.isEmpty()) {
                        Toast.makeText(getContext(), "Không có danh mục sản phẩm nào.", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "Đã tải " + categoryList.size() + " danh mục sản phẩm.");
                } else {
                    String errorMsg = "Lỗi khi tải danh mục sản phẩm: " + response.code();
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
            }

            @Override
            public void onFailure(Call<List<ProductCategoryResponseDto>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(getContext(), "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onEditClick(int categoryId) {
        Toast.makeText(getContext(), "Tính năng chỉnh sửa danh mục ID: " + categoryId + " sẽ được triển khai.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDeleteClick(int categoryId) {
        Toast.makeText(getContext(), "Tính năng xóa danh mục ID: " + categoryId + " sẽ được triển khai.", Toast.LENGTH_SHORT).show();

    }

}