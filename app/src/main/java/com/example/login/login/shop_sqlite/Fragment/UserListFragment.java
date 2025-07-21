package com.example.login.login.shop_sqlite.Fragment;

import android.content.Context; // Giữ lại Context cho Toast/Log
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.login.shop_sqlite.Adapter.UserAdapter;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.UserResponseDto;
import com.example.login.login.shop_sqlite.R;
// Không cần FloatingActionButton nếu không có nút thêm
// import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// UserListFragment sẽ KHÔNG implement các listener cho Create/Edit
public class UserListFragment extends Fragment implements UserAdapter.OnUserActionListener { // Vẫn giữ listener này nếu Adapter có thể click item

    private static final String TAG = "UserListFragment";

    private ListView lvUsers;
    // Bỏ FloatingActionButton fabCreateNewUser;
    private ApiService apiService;
    private List<UserResponseDto> userList = new ArrayList<>();
    private UserAdapter userAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Không cần kiểm tra listener nào ở đây
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_list, container, false);

        lvUsers = view.findViewById(R.id.lvUsers);
        // Không ánh xạ và xử lý sự kiện cho fabCreateNewUser nữa
        // fabCreateNewUser = view.findViewById(R.id.btnCreateNewUser);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Khởi tạo UserAdapter. LƯU Ý: Nếu Adapter của bạn có các nút Sửa/Xóa
        // và bạn không muốn chúng xuất hiện, bạn cần chỉnh sửa logic trong UserAdapter.
        userAdapter = new UserAdapter(getContext(), userList, this); // Vẫn truyền 'this' nếu UserAdapter cần OnUserActionListener cho các mục khác (ví dụ: click vào item)
        lvUsers.setAdapter(userAdapter);

        // Bỏ sự kiện click cho fabCreateNewUser
        /*
        fabCreateNewUser.setOnClickListener(v -> {
            CreateUserFragment createUserFragment = new CreateUserFragment();
            createUserFragment.setTargetFragment(UserListFragment.this, 0);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, createUserFragment)
                    .addToBackStack(null)
                    .commit();
        });
        */

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchUsers();
    }

    public void refreshUserList() {
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
                    String errorMsg = "Lỗi khi tải người dùng: " + response.code();
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
            }

            @Override
            public void onFailure(retrofit2.Call<List<UserResponseDto>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ:", t);
                Toast.makeText(getContext(), "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //region Implement UserAdapter.OnUserActionListener methods
    // Các phương thức này sẽ được gọi từ UserAdapter, nhưng UserListFragment sẽ không làm gì cả
    // (hoặc chỉ ghi log) vì bạn không muốn xử lý chỉnh sửa/xóa ở đây lúc này.
    @Override
    public void onEditClick(UserResponseDto user) {
        // Log hoặc Toast để kiểm tra xem có được gọi không, nhưng không thực hiện điều hướng
        Log.d(TAG, "Edit clicked for user: " + user.getUserName() + ". No action taken as per current requirement.");
        Toast.makeText(getContext(), "Tính năng chỉnh sửa tạm thời không khả dụng.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(UserResponseDto user) {
        // Log hoặc Toast để kiểm tra xem có được gọi không, nhưng không thực hiện điều hướng
        Log.d(TAG, "Delete clicked for user: " + user.getUserName() + ". No action taken as per current requirement.");
        Toast.makeText(getContext(), "Tính năng xóa tạm thời không khả dụng.", Toast.LENGTH_SHORT).show();
    }
    //endregion

    // Bỏ showDeleteConfirmationDialog và deleteUser nếu không dùng nữa
    /*
    private void showDeleteConfirmationDialog(int userId, String userName) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa Người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng '" + (userName != null ? userName : "ID: " + userId) + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(userId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteUser(int userId) {
        // ... (logic xóa người dùng) ...
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // Bỏ Implement CreateUserFragment.OnCreateUserSuccessListener và EditUserFragment.OnEditUserSuccessListener
    // Nếu bạn muốn bỏ hoàn toàn các chức năng này khỏi UserListFragment.
    /*
    @Override
    public void onCreateUserSuccess() {
        // ...
    }

    @Override
    public void onEditUserSuccess() {
        // ...
    }
    */
}