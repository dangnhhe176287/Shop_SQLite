package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Models.ResetPasswordRequestDto;
import com.example.login.login.shop_sqlite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText etEmail, etOtp, etNewPassword;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        // Nhận email và otp từ intent nếu có
        String emailFromIntent = getIntent().getStringExtra("email");
        String otpFromIntent = getIntent().getStringExtra("otp");
        if (emailFromIntent != null) {
            etEmail.setText(emailFromIntent);
        }
        if (otpFromIntent != null) {
            etOtp.setText(otpFromIntent);
        }

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String otp = etOtp.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Invalid email format");
                etEmail.requestFocus();
                return;
            }
            if (otp.isEmpty()) {
                etOtp.setError("OTP is required");
                etOtp.requestFocus();
                return;
            }
            if (otp.length() != 6) {
                etOtp.setError("OTP must be 6 digits");
                etOtp.requestFocus();
                return;
            }
            if (newPassword.isEmpty()) {
                etNewPassword.setError("New password is required");
                etNewPassword.requestFocus();
                return;
            }
            if (newPassword.length() < 6) {
                etNewPassword.setError("Password must be at least 6 characters");
                etNewPassword.requestFocus();
                return;
            }
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            ResetPasswordRequestDto request = new ResetPasswordRequestDto(email, otp, newPassword);
            btnResetPassword.setEnabled(false);
            apiService.resetPassword(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    btnResetPassword.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Password has been reset successfully",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Invalid or expired OTP code", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            });
        });
    }
}