package com.example.login.login.shop_sqlite.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Models.RegisterRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginResponseDto;
import com.example.login.login.shop_sqlite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etConfirmPassword, etUserName, etPhone, etDateOfBirth, etAddress;
    private Button btnRegister, btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etUserName = findViewById(R.id.etUserName);
        etPhone = findViewById(R.id.etPhone);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String userName = etUserName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String dob = etDateOfBirth.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Validate required fields
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Invalid email format");
                etEmail.requestFocus();
                return;
            } else {
                etEmail.setError(null);
            }

            if (userName.isEmpty()) {
                etUserName.setError("User name is required");
                etUserName.requestFocus();
                return;
            } else {
                etUserName.setError(null);
            }

            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            } else if (password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                etPassword.requestFocus();
                return;
            } else if (!password.matches(".*[A-Z].*")) {
                etPassword.setError("Password must contain at least one uppercase letter");
                etPassword.requestFocus();
                return;
            } else if (!password.matches(".*[a-z].*")) {
                etPassword.setError("Password must contain at least one lowercase letter");
                etPassword.requestFocus();
                return;
            } else if (!password.matches(".*[0-9].*")) {
                etPassword.setError("Password must contain at least one digit");
                etPassword.requestFocus();
                return;
            } else if (!password.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>/?-].*")) {
                etPassword.setError("Password must contain at least one special character");
                etPassword.requestFocus();
                return;
            } else {
                etPassword.setError(null);
            }

            if (confirmPassword.isEmpty()) {
                etConfirmPassword.setError("Please confirm your password");
                etConfirmPassword.requestFocus();
                return;
            } else if (!confirmPassword.equals(password)) {
                etConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                return;
            } else {
                etConfirmPassword.setError(null);
            }

            // Set empty strings to null for optional fields
            phone = phone.isEmpty() ? null : phone;
            dob = dob.isEmpty() ? null : dob;
            address = address.isEmpty() ? null : address;

            register(email, password, userName, phone, dob, address);
        });

        btnBackToLogin.setOnClickListener(v ->

        finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    etDateOfBirth.setText(date);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void register(String email, String password, String userName, String phone, String dob, String address) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        RegisterRequestDto registerRequest = new RegisterRequestDto(email, password, userName, phone, dob, address);
        Call<LoginResponseDto> call = apiService.register(registerRequest);
        call.enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().token;
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("token", token);
                    intent.putExtra("userName", response.body().UserName);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Register failed";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
