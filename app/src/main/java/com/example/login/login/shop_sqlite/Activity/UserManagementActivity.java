package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Models.UserDto;
import com.example.login.login.shop_sqlite.Dto.SaleUserResponseDto;
import com.example.login.login.shop_sqlite.Dto.SaleUserCreateDto;
import com.example.login.login.shop_sqlite.Activity.SaleUserDetailActivity;
import com.example.login.login.shop_sqlite.R;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.content.DialogInterface;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.ProgressBar;
import android.content.Intent;

public class UserManagementActivity extends AppCompatActivity {
    private RecyclerView rvUsers;
    private Button btnAddUser;
    private UserAdapter userAdapter;
    private List<SaleUserResponseDto> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        rvUsers = findViewById(R.id.rvUsers);
        btnAddUser = findViewById(R.id.btnAddUser);

        userAdapter = new UserAdapter(userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(SaleUserResponseDto user) {
                showUserDialog(user);
            }

            @Override
            public void onDelete(SaleUserResponseDto user) {
                new AlertDialog.Builder(UserManagementActivity.this)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onView(SaleUserResponseDto user) {
                Intent intent = new Intent(UserManagementActivity.this, SaleUserDetailActivity.class);
                intent.putExtra("userId", user.userId);
                startActivity(intent);
            }
        });
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);

        btnAddUser.setOnClickListener(v -> showUserDialog(null));

        loadUsers();
    }

    private void loadUsers() {
        SaleApiService saleApiService = ApiClient.getClient().create(SaleApiService.class);
        saleApiService.getAllUsers().enqueue(new Callback<List<SaleUserResponseDto>>() {
            @Override
            public void onResponse(Call<List<SaleUserResponseDto>> call, Response<List<SaleUserResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(UserManagementActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SaleUserResponseDto>> call, Throwable t) {
                Toast.makeText(UserManagementActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void showUserDialog(SaleUserResponseDto user) {
        boolean isEdit = user != null;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this,
                R.style.ThemeOverlay_Material3_MaterialAlertDialog);
        builder.setTitle(isEdit ? "Edit User" : "Add User");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user, null);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);
        EditText etUserName = view.findViewById(R.id.etUserName);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etAddress = view.findViewById(R.id.etAddress);
        EditText etDateOfBirth = view.findViewById(R.id.etDateOfBirth);
        if (isEdit) {
            etEmail.setText(user.email);
            etPassword.setText(user.password);
            etUserName.setText(user.userName);
            etPhone.setText(user.phone);
            etAddress.setText(user.address);
            etDateOfBirth.setText(user.dateOfBirth);
        }
        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String userName = etUserName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String dob = etDateOfBirth.getText().toString().trim();
                // Validate
                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Valid email required");
                    etEmail.requestFocus();
                    return;
                }
                if (userName.isEmpty()) {
                    etUserName.setError("User name required");
                    etUserName.requestFocus();
                    return;
                }
                if (password.isEmpty() || password.length() < 6 ||
                        !password.matches(".*[A-Z].*") ||
                        !password.matches(".*[a-z].*") ||
                        !password.matches(".*[0-9].*") ||
                        !password.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>/?-].*")) {
                    etPassword.setError("Password must be 6+ chars, upper, lower, digit, special char");
                    etPassword.requestFocus();
                    return;
                }
                SaleApiService saleApiService = ApiClient.getClient().create(SaleApiService.class);
                if (isEdit) {
                    SaleUserResponseDto editUser = user;
                    editUser.email = email;
                    editUser.password = password;
                    editUser.userName = userName;
                    editUser.phone = phone;
                    editUser.address = address;
                    editUser.dateOfBirth = dob;
                    saleApiService.updateUser(editUser.userId, editUser).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(rvUsers, "User updated", Snackbar.LENGTH_SHORT).show();
                                loadUsers();
                                dialog.dismiss();
                            } else {
                                Snackbar.make(rvUsers, "Update failed", Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                            Snackbar.make(rvUsers, "Network error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    SaleUserCreateDto newUser = new SaleUserCreateDto();
                    newUser.email = email;
                    newUser.password = password;
                    newUser.userName = userName;
                    newUser.phone = phone;
                    newUser.address = address;
                    newUser.dateOfBirth = dob;
                    newUser.roleId = 2;
                    newUser.status = 1;
                    newUser.isDelete = false;
                    saleApiService.createUser(newUser).enqueue(new retrofit2.Callback<SaleUserResponseDto>() {
                        @Override
                        public void onResponse(retrofit2.Call<SaleUserResponseDto> call, retrofit2.Response<SaleUserResponseDto> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(rvUsers, "User added", Snackbar.LENGTH_SHORT).show();
                                loadUsers();
                                dialog.dismiss();
                            } else {
                                String errorMsg = "Add failed";
                                try {
                                    if (response.errorBody() != null)
                                        errorMsg = response.errorBody().string();
                                } catch (Exception ignored) {
                                }
                                Snackbar.make(rvUsers, errorMsg, Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<SaleUserResponseDto> call, Throwable t) {
                            Snackbar.make(rvUsers, "Network error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
        dialog.show();
    }

    private void deleteUser(SaleUserResponseDto user) {
        SaleApiService saleApiService = ApiClient.getClient().create(SaleApiService.class);
        saleApiService.deleteUser(user.userId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserManagementActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(UserManagementActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Toast.makeText(UserManagementActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}