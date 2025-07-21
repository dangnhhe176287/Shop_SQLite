package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.Models.UserDto;
import com.example.login.login.shop_sqlite.R;

public class UserDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        TextView tvEmail = findViewById(R.id.tvDetailEmail);
        TextView tvUserName = findViewById(R.id.tvDetailUserName);
        TextView tvPhone = findViewById(R.id.tvDetailPhone);
        TextView tvAddress = findViewById(R.id.tvDetailAddress);
        TextView tvDateOfBirth = findViewById(R.id.tvDetailDateOfBirth);
        TextView tvStatus = findViewById(R.id.tvDetailStatus);
        TextView tvRoleId = findViewById(R.id.tvDetailRoleId);
        TextView tvCreateDate = findViewById(R.id.tvDetailCreateDate);

        UserDto user = (UserDto) getIntent().getSerializableExtra("user");
        if (user != null) {
            tvEmail.setText("Email: " + user.email);
            tvUserName.setText("User Name: " + user.userName);
            tvPhone.setText("Phone: " + (user.phone != null ? user.phone : ""));
            tvAddress.setText("Address: " + (user.address != null ? user.address : ""));
            tvDateOfBirth.setText("Date of Birth: " + (user.dateOfBirth != null ? user.dateOfBirth : ""));
            tvStatus.setText("Status: " + user.status);
            tvRoleId.setText("RoleId: " + user.roleId);
            tvCreateDate.setText("Create Date: " + (user.createDate != null ? user.createDate : ""));
        }
    }
}