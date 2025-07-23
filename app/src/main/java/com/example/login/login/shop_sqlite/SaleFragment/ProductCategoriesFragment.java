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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.login.shop_sqlite.Activity.SaleCreateCategoryActivity; // Import CreateCategoryActivity
import com.example.login.login.shop_sqlite.Activity.SaleUpdateCategoryActivity;
import com.example.login.login.shop_sqlite.R;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Adapter.SaleCategoryAdapter;
import com.example.login.login.shop_sqlite.Dto.SaleProductCategoryResponseDto;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCategoriesFragment extends Fragment implements SaleCategoryAdapter.OnCategoryActionListener {

    private static final String TAG = "ProductCategoriesFrag";
    private static final int REQUEST_CODE_UPDATE_CATEGORY = 101; // Request code for update activity
    private static final int REQUEST_CODE_CREATE_CATEGORY = 102; // New request code for create activity

    private TextView txtHeader;
    private Button btnAddCategory;
    private ListView listViewCategories;
    private SaleApiService saleApiService;
    private List<SaleProductCategoryResponseDto> categoryList = new ArrayList<>();
    private SaleCategoryAdapter saleCategoryAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sale_activity_product_categories, container, false);

        txtHeader = view.findViewById(R.id.txtHeader);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        listViewCategories = view.findViewById(R.id.listViewCategories);

        txtHeader.setText("Danh mục Sản phẩm");

        saleApiService = ApiClient.getClient().create(SaleApiService.class);

        saleCategoryAdapter = new SaleCategoryAdapter(getContext(), categoryList, this);
        listViewCategories.setAdapter(saleCategoryAdapter);

        btnAddCategory.setOnClickListener(v -> {
            // Launch CreateCategoryActivity when "Add New Category" button is clicked
            Intent intent = new Intent(getContext(), SaleCreateCategoryActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_CATEGORY); // Use the new request code
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchProductCategories(); // Initial load of categories
    }

    public void refreshProductCategoryList() {
        fetchProductCategories(); // Call this when you need to refresh the list
    }

    private void fetchProductCategories() {
        Log.d(TAG, "Đang tải danh sách danh mục sản phẩm...");
        saleApiService.getAllProductCategories().enqueue(new Callback<List<SaleProductCategoryResponseDto>>() {
            @Override
            public void onResponse(Call<List<SaleProductCategoryResponseDto>> call, Response<List<SaleProductCategoryResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    saleCategoryAdapter.notifyDataSetChanged();

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
            public void onFailure(Call<List<SaleProductCategoryResponseDto>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(getContext(), "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(int categoryId) {
        Log.d(TAG, "Đã nhấp chỉnh sửa danh mục ID: " + categoryId);
        Intent intent = new Intent(getContext(), SaleUpdateCategoryActivity.class);
        intent.putExtra("categoryId", categoryId);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_CATEGORY);
    }

    @Override
    public void onDeleteClick(int categoryId) {
        Log.d(TAG, "Đã nhấp xóa danh mục ID: " + categoryId);

        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa danh mục này không?")
                .setPositiveButton("Xóa", (dialog, which) -> confirmDeleteCategory(categoryId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmDeleteCategory(int categoryId) {
        saleApiService.deleteCategory(categoryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa danh mục thành công!", Toast.LENGTH_SHORT).show();
                    refreshProductCategoryList();
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Không tìm thấy danh mục để xóa.", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Lỗi khi xóa danh mục: " + response.code();
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
        if (resultCode == getActivity().RESULT_OK) { // Check if the activity returned RESULT_OK
            if (requestCode == REQUEST_CODE_UPDATE_CATEGORY) {
                refreshProductCategoryList();
                Toast.makeText(getContext(), "Danh mục đã được cập nhật!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_CODE_CREATE_CATEGORY) {
                refreshProductCategoryList();
                Toast.makeText(getContext(), "Danh mục mới đã được tạo!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}