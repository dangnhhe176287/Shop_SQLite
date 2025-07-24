package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.login.login.shop_sqlite.Models.UserDto;
import com.example.login.login.shop_sqlite.Models.UpdateUserDto;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.R;
import com.example.login.login.shop_sqlite.Models.ChangePasswordRequestDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private EditText etName, etEmail, etPhone, etAddress;
    private Button btnSave, btnCancel;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private int userId;
    private UserDto currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Sửa thông tin cá nhân");
        }

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("current_user_id", 0);

        if (userId == 0) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserProfile();

        btnSave.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> finish());
        btnChangePassword.setOnClickListener(v -> handleChangePassword());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadUserProfile() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getUserByIdForEdit(userId).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    populateFields();
                } else {
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    Toast.makeText(EditProfileActivity.this, "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Log.e(TAG, "API Call Failed", t);
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields() {
        if (currentUser != null) {
            etName.setText(currentUser.userName);
            etEmail.setText(currentUser.email);
            etPhone.setText(currentUser.phone);
            etAddress.setText(currentUser.address);
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Tên không được để trống");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email không được để trống");
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Số điện thoại không được để trống");
            return;
        }
        UpdateUserDto updatedUser = new UpdateUserDto();
        updatedUser.userId = currentUser.userId;
        updatedUser.userName = name;
        updatedUser.email = email;
        updatedUser.phone = phone;
        updatedUser.address = address;
        updatedUser.password = currentUser.password;
        updatedUser.roleId = currentUser.roleId;
        updatedUser.status = currentUser.status;
        updatedUser.isDelete = currentUser.isDelete;
        updatedUser.dateOfBirth = currentUser.dateOfBirth;
        updatedUser.createDate = currentUser.createDate;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Log.d(TAG, "Updating user with ID: " + userId);
        Log.d(TAG, "Updated user data: " + updatedUser.userName + ", " + updatedUser.email + ", " + updatedUser.phone
                + ", " + updatedUser.address);

        apiService.updateUser(userId, updatedUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Update API Response: " + response.code() + " - " + response.message());
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT)
                            .show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Log.e(TAG, "Update API Error: " + response.code() + " - " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string()
                                : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Update API Call Failed", t);
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối khi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() > 6 &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?].*");
    }

    private void handleChangePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (oldPassword.isEmpty()) {
            etOldPassword.setError("Vui lòng nhập mật khẩu cũ");
            return;
        }
        if (newPassword.isEmpty()) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }
        if (!isValidPassword(newPassword)) {
            etNewPassword.setError(
                    "Mật khẩu phải lớn hơn 6 ký tự, có ít nhất 1 chữ thường, 1 chữ hoa, 1 số, 1 ký tự đặc biệt");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Xác nhận mật khẩu không khớp");
            return;
        }
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        ChangePasswordRequestDto req = new ChangePasswordRequestDto();
        req.userId = userId;
        req.oldPassword = oldPassword;
        req.newPassword = newPassword;
        btnChangePassword.setEnabled(false);
        apiService.changePassword(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnChangePassword.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    etOldPassword.setText("");
                    etNewPassword.setText("");
                    etConfirmPassword.setText("");
                } else {
                    Toast.makeText(EditProfileActivity.this, "Đổi mật khẩu thất bại: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnChangePassword.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}