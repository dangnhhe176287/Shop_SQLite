package com.example.login.login.shop_sqlite.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.login.login.shop_sqlite.Activity.CreateUserActivity;
import com.example.login.login.shop_sqlite.Activity.EditUserActivity;
import com.example.login.login.shop_sqlite.Adapter.UserAdapter;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.UserResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements UserAdapter.OnUserActionListener {

    private static final String TAG = "UserListFragment";
    private static final int REQUEST_CODE_CREATE_USER = 1;
    private static final int REQUEST_CODE_EDIT_USER = 2;

    private ListView lvUsers;
    private ApiService apiService;
    private List<UserResponseDto> userList = new ArrayList<>();
    private UserAdapter userAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_list, container, false);

        lvUsers = view.findViewById(R.id.lvUsers);
        apiService = ApiClient.getClient().create(ApiService.class);
        userAdapter = new UserAdapter(getContext(), userList, this);
        lvUsers.setAdapter(userAdapter);

        view.findViewById(R.id.btnCreateUser).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateUserActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_USER);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchUsers();
    }

    private void fetchUsers() {
        Log.d(TAG, "Đang tải danh sách người dùng...");
        apiService.getAllUsers().enqueue(new retrofit2.Callback<List<UserResponseDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<UserResponseDto>> call, retrofit2.Response<List<UserResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    userAdapter.notifyDataSetChanged();

                    if (userList.isEmpty()) {
                        Toast.makeText(getContext(), "Không có người dùng nào.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showError(response);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<UserResponseDto>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(getContext(), "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showError(retrofit2.Response<?> response) {
        String errorMsg = "Lỗi: " + response.code();
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
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEditClick(UserResponseDto user) {
        Intent intent = new Intent(getActivity(), EditUserActivity.class);
        intent.putExtra(EditUserActivity.EXTRA_USER_ID, user.getUserId());
        startActivityForResult(intent, REQUEST_CODE_EDIT_USER);
    }

    @Override
    public void onDeleteClick(UserResponseDto user) {
        apiService.deleteUser(user.getUserId()).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa người dùng thành công!", Toast.LENGTH_SHORT).show();
                    fetchUsers();
                } else {
                    showError(response);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ khi xóa người dùng:", t);
                Toast.makeText(getContext(), "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_USER && resultCode == AppCompatActivity.RESULT_OK) {
            fetchUsers();  // Tải lại danh sách khi tạo người dùng mới
        } else if (requestCode == REQUEST_CODE_EDIT_USER && resultCode == AppCompatActivity.RESULT_OK) {
            fetchUsers();  // Tải lại danh sách khi chỉnh sửa người dùng
        }
    }
}