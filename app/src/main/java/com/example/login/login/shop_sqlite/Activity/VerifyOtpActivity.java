package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Models.VerifyOtpRequestDto;
import com.example.login.login.shop_sqlite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {
    private EditText etEmail, etOtp;
    private Button btnVerifyOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);

        // Nhận email từ intent nếu có
        String emailFromIntent = getIntent().getStringExtra("email");
        if (emailFromIntent != null) {
            etEmail.setText(emailFromIntent);
        }

        btnVerifyOtp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String otp = etOtp.getText().toString().trim();
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
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            VerifyOtpRequestDto request = new VerifyOtpRequestDto(email, otp);
            btnVerifyOtp.setEnabled(false);
            apiService.verifyOtp(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    btnVerifyOtp.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(VerifyOtpActivity.this, "OTP verified successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("otp", otp);
                        startActivity(intent);
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, "Invalid or expired OTP code", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    btnVerifyOtp.setEnabled(true);
                    Toast.makeText(VerifyOtpActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            });
        });
    }
}