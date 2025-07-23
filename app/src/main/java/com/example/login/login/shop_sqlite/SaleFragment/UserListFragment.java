package com.example.login.login.shop_sqlite.SaleFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Import AlertDialog for consistency
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.login.login.shop_sqlite.Activity.SaleCreateUserActivity;
import com.example.login.login.shop_sqlite.Activity.SaleEditUserActivity;
import com.example.login.login.shop_sqlite.Activity.SaleUserDetailActivity; // Import UserDetailActivity
import com.example.login.login.shop_sqlite.Adapter.SaleUserAdapter;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Dto.SaleUserResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements SaleUserAdapter.OnUserActionListener {

    private static final String TAG = "UserListFragment";
    private static final int REQUEST_CODE_CREATE_USER = 1;
    private static final int REQUEST_CODE_EDIT_USER = 2;
    // Define a request code for detail view if needed (less common for fragments)
    // private static final int REQUEST_CODE_DETAIL_USER = 3;

    private ListView lvUsers;
    private SaleApiService saleApiService;
    private List<SaleUserResponseDto> userList = new ArrayList<>();
    private SaleUserAdapter saleUserAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sale_activity_user_list, container, false);

        lvUsers = view.findViewById(R.id.lvUsers);
        saleApiService = ApiClient.getClient().create(SaleApiService.class);
        saleUserAdapter = new SaleUserAdapter(getContext(), userList, this);
        lvUsers.setAdapter(saleUserAdapter);

        // Assuming btnCreateUser is the FloatingActionButton ID as per UserListActivity
        view.findViewById(R.id.btnCreateUser).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SaleCreateUserActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_USER);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchUsers();
    }

    private void fetchUsers() {
        Log.d(TAG, "Đang tải danh sách người dùng...");
        saleApiService.getAllUsers().enqueue(new retrofit2.Callback<List<SaleUserResponseDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<SaleUserResponseDto>> call, retrofit2.Response<List<SaleUserResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    saleUserAdapter.notifyDataSetChanged();

                    if (userList.isEmpty()) {
                        Toast.makeText(getContext(), "Không có người dùng nào.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showError(response);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<SaleUserResponseDto>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(getContext(), "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showError(retrofit2.Response<?> response) {
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
    public void onEditClick(SaleUserResponseDto user) {
        Intent intent = new Intent(getActivity(), SaleEditUserActivity.class);
        intent.putExtra(SaleEditUserActivity.EXTRA_USER_ID, user.getUserId());
        startActivityForResult(intent, REQUEST_CODE_EDIT_USER);
    }

    @Override
    public void onDeleteClick(SaleUserResponseDto user) {
        // Use AlertDialog from AppCompat for consistency with Activity
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa Người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng '" + (user.getUserName() != null ? user.getUserName() : "ID: " + user.getUserId()) + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> confirmDeleteUser(user.getUserId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    // --- Start of new implementation for onDetailClick ---
    @Override
    public void onDetailClick(SaleUserResponseDto user) {
        Log.d(TAG, "Đã nhấp xem chi tiết người dùng ID: " + user.getUserId());
        Intent intent = new Intent(getActivity(), SaleUserDetailActivity.class);
        intent.putExtra("userId", user.getUserId()); // Ensure UserDetailActivity expects "userId"
        startActivity(intent); // No need for startActivityForResult
    }
    // --- End of new implementation for onDetailClick ---

    private void confirmDeleteUser(int userId) {
        saleApiService.deleteUser(userId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa người dùng thành công!", Toast.LENGTH_SHORT).show();
                    fetchUsers();
                } else {
                    showError(response);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ khi xóa người dùng:", t);
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
        if (resultCode == AppCompatActivity.RESULT_OK) { // Check if the operation was successful
            if (requestCode == REQUEST_CODE_CREATE_USER || requestCode == REQUEST_CODE_EDIT_USER) {
                fetchUsers();  // Tải lại danh sách khi tạo hoặc chỉnh sửa người dùng
                Toast.makeText(getContext(), "Danh sách người dùng đã được cập nhật.", Toast.LENGTH_SHORT).show();
            }
            // No specific handling for DETAIL_USER_REQUEST_CODE as it doesn't return a result
        }
    }
}