package com.example.login.login.shop_sqlite.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Models.UserProfileDto;
import com.example.login.login.shop_sqlite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUserName, tvEmail, tvPhone, tvAddress, tvDateOfBirth, tvCreateDate, tvStatus, tvRoleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvCreateDate = findViewById(R.id.tvCreateDate);
        tvStatus = findViewById(R.id.tvStatus);
        tvRoleId = findViewById(R.id.tvRoleId);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("current_user_id", -1);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getUserById(userId).enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(Call<UserProfileDto> call, Response<UserProfileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileDto profile = response.body();
                    tvUserName.setText(profile.userName);
                    tvEmail.setText(profile.email);
                    tvPhone.setText(profile.phone);
                    tvAddress.setText(profile.address);
                    tvDateOfBirth.setText(profile.dateOfBirth);
                    tvCreateDate.setText(profile.createDate);
                    tvStatus.setText(String.valueOf(profile.status));
                    tvRoleId.setText(String.valueOf(profile.roleId));
                } else {
                    Toast.makeText(ProfileActivity.this, "Không lấy được thông tin profile", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 