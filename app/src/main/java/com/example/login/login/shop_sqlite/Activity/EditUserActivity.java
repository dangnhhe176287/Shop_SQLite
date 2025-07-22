package com.example.login.login.shop_sqlite.Activity;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.UserResponseDto; // Sử dụng UserResponseDto cho cả đọc và gửi cập nhật
import com.example.login.login.shop_sqlite.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {

    private static final String TAG = "EditUserActivity";
    public static final String EXTRA_USER_ID = "extra_user_id";

    private EditText edtUserId, edtRoleId, edtEmail, edtPassword, edtPhone,
            edtUserName, edtDateOfBirth, edtAddress, edtCreateDate, edtStatus, edtIsDelete;
    private Button btnSaveUser;

    private ApiService apiService;
    private int currentUserId;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Ánh xạ các thành phần UI
        edtUserId = findViewById(R.id.edtUserId);
        edtRoleId = findViewById(R.id.edtRoleId);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtUserName = findViewById(R.id.edtUserName);
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth);
        edtAddress = findViewById(R.id.edtAddress);
        edtCreateDate = findViewById(R.id.edtCreateDate);
        edtStatus = findViewById(R.id.edtStatus);
        edtIsDelete = findViewById(R.id.edtIsDelete);

        btnSaveUser = findViewById(R.id.btnSaveUser);

        apiService = ApiClient.getClient().create(ApiService.class);
        calendar = Calendar.getInstance();

        currentUserId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người dùng để chỉnh sửa.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        edtUserId.setEnabled(false);
        edtCreateDate.setEnabled(false);

        edtDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        btnSaveUser.setOnClickListener(v -> saveUser());

        fetchUserDetails(currentUserId);
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateOfBirthField();
        };

        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateDateOfBirthField() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        edtDateOfBirth.setText(sdf.format(calendar.getTime()));
    }

    private void fetchUserDetails(int userId) {
        apiService.saleGetUserById(userId).enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponseDto user = response.body();
                    populateUserFields(user);
                } else {
                    String errorMsg = "Lỗi khi tải chi tiết người dùng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - Chi tiết: " + response.errorBody().string();
                        }
                    } catch (Exception e) {   }
                    Toast.makeText(EditUserActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                Toast.makeText(EditUserActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void populateUserFields(UserResponseDto user) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        edtUserId.setText(String.valueOf(user.getUserId()));
        edtRoleId.setText(String.valueOf(user.getRoleId()));
        edtEmail.setText(user.getEmail());
        edtPassword.setText(user.getPassword());
        edtPhone.setText(user.getPhone());
        edtUserName.setText(user.getUserName());

        if (user.getDateOfBirth() != null) {
            edtDateOfBirth.setText(sdf.format(user.getDateOfBirth()));
            calendar.setTime(user.getDateOfBirth());
        }
        edtAddress.setText(user.getAddress());
        if (user.getCreateDate() != null) {
            edtCreateDate.setText(sdf.format(user.getCreateDate()));
        }
        edtStatus.setText(String.valueOf(user.getStatus()));
        edtIsDelete.setText(String.valueOf(user.isIsDelete()));
    }

    private void saveUser() {
        String userIdStr = edtUserId.getText().toString().trim();
        String roleIdStr = edtRoleId.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String userName = edtUserName.getText().toString().trim();
        String dateOfBirthStr = edtDateOfBirth.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String createDateStr = edtCreateDate.getText().toString().trim();
        String statusStr = edtStatus.getText().toString().trim();
        String isDeleteStr = edtIsDelete.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || roleIdStr.isEmpty() || statusStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ các thông tin bắt buộc.", Toast.LENGTH_LONG).show();
            return;
        }

        int userId, roleId, status;
        boolean isDelete;
        Date dateOfBirth, createDate;

        try {
            userId = Integer.parseInt(userIdStr);
            roleId = Integer.parseInt(roleIdStr);
            status = Integer.parseInt(statusStr);
            isDelete = Boolean.parseBoolean(isDeleteStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID, Status phải là số. Is Deleted phải là 'true' hoặc 'false'.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            dateOfBirth = sdf.parse(dateOfBirthStr);
            createDate = sdf.parse(createDateStr);
        } catch (java.text.ParseException e) {
            Toast.makeText(this, "Ngày tháng không hợp lệ. Vui lòng kiểm tra lại.", Toast.LENGTH_LONG).show();
            return;
        }

        UserResponseDto userUpdateDto = new UserResponseDto(
                userId,
                roleId,
                email,
                password,
                phone.isEmpty() ? null : phone,
                userName.isEmpty() ? null : userName,
                dateOfBirth,
                address.isEmpty() ? null : address,
                createDate,
                status,
                isDelete
        );

        apiService.updateUser(userId, userUpdateDto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUserActivity.this, "Cập nhật người dùng thành công!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = "Lỗi khi cập nhật người dùng: " + response.code();
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
                    Toast.makeText(EditUserActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(EditUserActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}