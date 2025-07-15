package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Models.LoginRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginResponseDto;
import com.example.login.login.shop_sqlite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private CheckBox cbRememberMe;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Khi mở màn hình, tự động điền lại nếu đã lưu
        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("email", "");
        String savedPassword = prefs.getString("password", "");
        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            etEmail.setText(savedEmail);
            etPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cbRememberMe.isChecked()) {
                SharedPreferences.Editor editor = getSharedPreferences("login_prefs", MODE_PRIVATE).edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();
            } else {
                SharedPreferences.Editor editor = getSharedPreferences("login_prefs", MODE_PRIVATE).edit();
                editor.remove("email");
                editor.remove("password");
                editor.apply();
            }
            login(email, password);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void login(String email, String password) {
        Log.d("LoginActivity", "Attempting login with email: " + email);
        Log.d("LoginActivity", "API URL: http://10.0.2.2:5287/");
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequestDto loginRequest = new LoginRequestDto(email, password);
        Call<LoginResponseDto> call = apiService.login(loginRequest);
        
        Log.d("LoginActivity", "Making API call...");
        
        call.enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                Log.d("LoginActivity", "Response received. Code: " + response.code());
                Log.d("LoginActivity", "Response body: " + response.body());
                Log.d("LoginActivity", "Error body: " + response.errorBody());
                
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().token;
                    int userId = response.body().userId;
                    Log.d("LoginActivity", "Login successful. Token: " + token);
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                    // Lưu userId vào SharedPreferences
                    android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    prefs.edit().putInt("current_user_id", userId).apply();

                    // Navigate directly to ProductListActivity
                    Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
                    intent.putExtra("token", token);
                    intent.putExtra("userName", response.body().UserName);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Login failed";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                            Log.e("LoginActivity", "Error response: " + errorMsg);
                        } catch (Exception e) {
                            Log.e("LoginActivity", "Error reading error body", e);
                        }
                    }
                    Log.e("LoginActivity", "Login failed with code: " + response.code());
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                Log.e("LoginActivity", "Network error", t);
                String errorMessage = "Network error: " + t.getMessage();
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage = "Connection timeout. Please check your internet connection and try again.";
                } else if (t instanceof java.net.ConnectException) {
                    errorMessage = "Cannot connect to server. Please make sure the backend is running.";
                }
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
