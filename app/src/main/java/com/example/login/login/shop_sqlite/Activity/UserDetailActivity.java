// com.example.login.login.shop_sqlite.Activity.UserDetailActivity.java
package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.UserResponseDto; // Ensure this DTO exists
import com.example.login.login.shop_sqlite.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    private static final String TAG = "UserDetailActivity";

    private TextView tvUserId, tvUserNameDetail, tvUserEmailDetail, tvUserPhoneDetail,
            tvUserRoleDetail, tvUserDateOfBirth, tvUserAddress, tvUserCreateDate,
            tvUserStatus, tvUserIsDelete;

    private ApiService apiService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Initialize TextViews
        tvUserId = findViewById(R.id.tvUserId);
        tvUserNameDetail = findViewById(R.id.tvUserNameDetail);
        tvUserEmailDetail = findViewById(R.id.tvUserEmailDetail);
        tvUserPhoneDetail = findViewById(R.id.tvUserPhoneDetail);
        tvUserRoleDetail = findViewById(R.id.tvUserRoleDetail);
        tvUserDateOfBirth = findViewById(R.id.tvUserDateOfBirth);
        tvUserAddress = findViewById(R.id.tvUserAddress);
        tvUserCreateDate = findViewById(R.id.tvUserCreateDate);
        tvUserStatus = findViewById(R.id.tvUserStatus);
        tvUserIsDelete = findViewById(R.id.tvUserIsDelete);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Get userId from Intent
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng để xem chi tiết.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "userId là -1. Không thể xem chi tiết.");
            finish(); // Close activity if no ID
            return;
        }

        fetchUserDetails(userId);
    }

    private void fetchUserDetails(int id) {
        Log.d(TAG, "Đang tải chi tiết người dùng với ID: " + id);
        apiService.getUserById(id).enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponseDto user = response.body();
                    Log.d(TAG, "Đã tải thành công chi tiết người dùng: " + user.getUserName());
                    displayUserDetails(user);
                } else {
                    String errorMsg = "Lỗi khi tải chi tiết người dùng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - Chi tiết: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody: " + e.getMessage(), e);
                        errorMsg += " - Không thể đọc chi tiết lỗi.";
                    }
                    Toast.makeText(UserDetailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onResponse Error khi tải chi tiết: " + errorMsg);
                    finish(); // Close activity if details can't be loaded
                }
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi tải chi tiết người dùng: " + t.getMessage(), t);
                Toast.makeText(UserDetailActivity.this, "Lỗi kết nối server khi tải chi tiết người dùng.", Toast.LENGTH_LONG).show();
                finish(); // Close activity if connection error
            }
        });
    }

    private void displayUserDetails(UserResponseDto user) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());


        tvUserId.setText("ID Người dùng: #" + user.getUserId());
        tvUserNameDetail.setText("Tên người dùng: " + (user.getUserName() != null ? user.getUserName() : "N/A"));
        tvUserEmailDetail.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
        tvUserPhoneDetail.setText("Số điện thoại: " + (user.getPhone() != null ? user.getPhone() : "N/A"));
        tvUserRoleDetail.setText("ID Vai trò: " + user.getRoleId()); // Display Role ID

        if (user.getDateOfBirth() != null) {
            tvUserDateOfBirth.setText("Ngày sinh: " + dateFormat.format(user.getDateOfBirth()));
        } else {
            tvUserDateOfBirth.setText("Ngày sinh: N/A");
        }

        tvUserAddress.setText("Địa chỉ: " + (user.getAddress() != null ? user.getAddress() : "N/A"));

        if (user.getCreateDate() != null) {
            tvUserCreateDate.setText("Ngày tạo tài khoản: " + dateTimeFormat.format(user.getCreateDate()));
        } else {
            tvUserCreateDate.setText("Ngày tạo tài khoản: N/A");
        }

        String statusText;
        switch (user.getStatus()) {
            case 1: statusText = "Hoạt động"; break;
            case 0: statusText = "Không hoạt động"; break;
            default: statusText = "Không xác định"; break;
        }
        tvUserStatus.setText("Trạng thái: " + statusText);

        tvUserIsDelete.setText("Đã xóa: " + (user.isIsDelete() ? "Có" : "Không"));
    }
}