package com.example.login.login.shop_sqlite.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button; // Không dùng button này nữa, dùng FloatingActionButton
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Adapter.UserAdapter; // Import UserAdapter
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.UserResponseDto;
import com.example.login.login.shop_sqlite.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton; // Import FAB

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private static final String TAG = "UserListActivity";
    private static final int CREATE_USER_REQUEST_CODE = 1;
    private static final int EDIT_USER_REQUEST_CODE = 2;

    private ListView lvUsers;
    private FloatingActionButton btnCreateNewUser;
    private ApiService apiService;
    private List<UserResponseDto> userList = new ArrayList<>();
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        lvUsers = findViewById(R.id.lvUsers);
        btnCreateNewUser = findViewById(R.id.btnCreateUser);

        apiService = ApiClient.getClient().create(ApiService.class);

        userAdapter = new UserAdapter(this, userList, this);
        lvUsers.setAdapter(userAdapter);


        btnCreateNewUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserListActivity.this, CreateUserActivity.class);
            startActivityForResult(intent, CREATE_USER_REQUEST_CODE);
        });

        fetchUsers();
    }

    private void fetchUsers() {
        Log.d(TAG, "Đang tải danh sách người dùng...");
        apiService.getAllUsers().enqueue(new Callback<List<UserResponseDto>>() {
            @Override
            public void onResponse(Call<List<UserResponseDto>> call, Response<List<UserResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());

                    userAdapter.notifyDataSetChanged();

                    if (userList.isEmpty()) {
                        Toast.makeText(UserListActivity.this, "Không có người dùng nào.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(UserListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserResponseDto>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(UserListActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(UserResponseDto user) {
        Intent intent = new Intent(UserListActivity.this, EditUserActivity.class);
        intent.putExtra(EditUserActivity.EXTRA_USER_ID, user.getUserId());
        startActivityForResult(intent, EDIT_USER_REQUEST_CODE);
    }

    @Override
    public void onDeleteClick(UserResponseDto user) {
        showDeleteConfirmationDialog(user.getUserId(), user.getUserName());
    }


    private void showDeleteConfirmationDialog(int userId, String userName) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng '" + (userName != null ? userName : "ID: " + userId) + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(userId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteUser(int userId) {
        apiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserListActivity.this, "Đã xóa người dùng thành công.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(UserListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ khi xóa:", t);
                Toast.makeText(UserListActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Người dùng mới đã được tạo.", Toast.LENGTH_SHORT).show();
            fetchUsers();
        } else if (requestCode == EDIT_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Người dùng đã được cập nhật.", Toast.LENGTH_SHORT).show();
            fetchUsers();
        }
    }
}