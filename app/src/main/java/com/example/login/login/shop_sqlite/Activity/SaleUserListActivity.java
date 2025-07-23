// com.example.login.login.shop_sqlite.Activity.UserListActivity.java
package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Adapter.SaleUserAdapter; // Import UserAdapter
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Dto.SaleUserResponseDto;
import com.example.login.login.shop_sqlite.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton; // Import FAB

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleUserListActivity extends AppCompatActivity implements SaleUserAdapter.OnUserActionListener {

    private static final String TAG = "UserListActivity";
    private static final int CREATE_USER_REQUEST_CODE = 1;
    private static final int EDIT_USER_REQUEST_CODE = 2;
    // Define a request code for detail view if you ever need a result back (less common for detail views)
    // private static final int DETAIL_USER_REQUEST_CODE = 3;


    private ListView lvUsers;
    private FloatingActionButton btnCreateNewUser;
    private SaleApiService saleApiService;
    private List<SaleUserResponseDto> userList = new ArrayList<>();
    private SaleUserAdapter saleUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_user_list);

        lvUsers = findViewById(R.id.lvUsers);
        btnCreateNewUser = findViewById(R.id.btnCreateUser);

        saleApiService = ApiClient.getClient().create(SaleApiService.class);

        saleUserAdapter = new SaleUserAdapter(this, userList, this);
        lvUsers.setAdapter(saleUserAdapter);


        btnCreateNewUser.setOnClickListener(v -> {
            Intent intent = new Intent(SaleUserListActivity.this, SaleCreateUserActivity.class);
            startActivityForResult(intent, CREATE_USER_REQUEST_CODE);
        });

        fetchUsers();
    }

    private void fetchUsers() {
        Log.d(TAG, "Đang tải danh sách người dùng...");
        saleApiService.getAllUsers().enqueue(new Callback<List<SaleUserResponseDto>>() {
            @Override
            public void onResponse(Call<List<SaleUserResponseDto>> call, Response<List<SaleUserResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());

                    saleUserAdapter.notifyDataSetChanged();

                    if (userList.isEmpty()) {
                        Toast.makeText(SaleUserListActivity.this, "Không có người dùng nào.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String errorMsg = "Lỗi khi tải người dùng: " + response.code();
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
                    Toast.makeText(SaleUserListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<SaleUserResponseDto>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(SaleUserListActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(SaleUserResponseDto user) {
        Intent intent = new Intent(SaleUserListActivity.this, SaleEditUserActivity.class);
        intent.putExtra(SaleEditUserActivity.EXTRA_USER_ID, user.getUserId());
        startActivityForResult(intent, EDIT_USER_REQUEST_CODE);
    }

    @Override
    public void onDeleteClick(SaleUserResponseDto user) {
        showDeleteConfirmationDialog(user.getUserId(), user.getUserName());
    }

    // --- Start of new implementation for onDetailClick ---
    @Override
    public void onDetailClick(SaleUserResponseDto user) {
        Log.d(TAG, "Đã nhấp xem chi tiết người dùng ID: " + user.getUserId());
        Intent intent = new Intent(SaleUserListActivity.this, SaleUserDetailActivity.class);
        intent.putExtra("userId", user.getUserId()); // Ensure UserDetailActivity expects "userId"
        startActivity(intent); // No need for startActivityForResult as no result is expected
    }
    // --- End of new implementation for onDetailClick ---


    private void showDeleteConfirmationDialog(int userId, String userName) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng '" + (userName != null ? userName : "ID: " + userId) + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(userId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteUser(int userId) {
        saleApiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SaleUserListActivity.this, "Đã xóa người dùng thành công.", Toast.LENGTH_SHORT).show();
                    fetchUsers();
                } else {
                    String errorMsg = "Lỗi khi xóa người dùng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String rawErrorBody = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + rawErrorBody;
                            Log.e(TAG, "Lỗi xóa API (" + response.code() + "): " + rawErrorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody khi xóa: " + e.getMessage(), e);
                        errorMsg += " - Không thể đọc chi tiết lỗi.";
                    }
                    Toast.makeText(SaleUserListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ khi xóa:", t);
                Toast.makeText(SaleUserListActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // Check if the operation was successful
            if (requestCode == CREATE_USER_REQUEST_CODE) {
                Toast.makeText(this, "Người dùng mới đã được tạo.", Toast.LENGTH_SHORT).show();
                fetchUsers();
            } else if (requestCode == EDIT_USER_REQUEST_CODE) {
                Toast.makeText(this, "Người dùng đã được cập nhật.", Toast.LENGTH_SHORT).show();
                fetchUsers();
            }
            // No specific handling for DETAIL_USER_REQUEST_CODE as it doesn't return a result
        }
    }
}