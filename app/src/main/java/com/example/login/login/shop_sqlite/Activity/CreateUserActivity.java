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
import com.example.login.login.shop_sqlite.Dto.UserCreateDto;
import com.example.login.login.shop_sqlite.Dto.UserResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUserActivity extends AppCompatActivity {

    private static final String TAG = "CreateUserActivity";

    private EditText edtRoleId, edtEmail, edtPassword, edtPhone, edtUserName, edtDateOfBirth, edtAddress, edtStatus, edtIsDelete;
    private Button btnCreateUser;

    private ApiService apiService;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        edtRoleId = findViewById(R.id.edtRoleId);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtUserName = findViewById(R.id.edtUserName);
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth);
        edtAddress = findViewById(R.id.edtAddress);
        edtStatus = findViewById(R.id.edtStatus);
        edtIsDelete = findViewById(R.id.edtIsDelete);

        btnCreateUser = findViewById(R.id.btnCreateUser);

        apiService = ApiClient.getClient().create(ApiService.class);
        calendar = Calendar.getInstance();

        edtDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        btnCreateUser.setOnClickListener(v -> createUser());
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

    private void createUser() {
        String roleIdStr = edtRoleId.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String userName = edtUserName.getText().toString().trim();
        String dateOfBirthStr = edtDateOfBirth.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String statusStr = edtStatus.getText().toString().trim();
        String isDeleteStr = edtIsDelete.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || roleIdStr.isEmpty() || statusStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ các thông tin bắt buộc (Email, Password, Role ID, Status).", Toast.LENGTH_LONG).show();
            return;
        }

        int roleId;
        int status;
        boolean isDelete;
        Date dateOfBirth;

        try {
            roleId = Integer.parseInt(roleIdStr);
            status = Integer.parseInt(statusStr);
            isDelete = Boolean.parseBoolean(isDeleteStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Role ID, Status phải là số. IsDelete phải là 'true' hoặc 'false'.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            dateOfBirth = sdf.parse(dateOfBirthStr);
        } catch (java.text.ParseException e) {
            Toast.makeText(this, "Ngày sinh không hợp lệ. Vui lòng chọn lại.", Toast.LENGTH_LONG).show();
            return;
        }

        UserCreateDto userCreateDto = new UserCreateDto(
                roleId,
                email,
                password,
                phone.isEmpty() ? null : phone,
                userName.isEmpty() ? null : userName,
                dateOfBirth,
                address.isEmpty() ? null : address,
                status,
                isDelete
        );

        Log.d(TAG, "Gửi UserCreateDto: " + new com.google.gson.Gson().toJson(userCreateDto));

        apiService.createUser(userCreateDto).enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateUserActivity.this, "Tạo người dùng thành công! ID: " + response.body().getUserId(), Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = "Lỗi khi tạo người dùng: " + response.code();
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
                    Toast.makeText(CreateUserActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(CreateUserActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}