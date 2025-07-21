package com.example.login.login.shop_sqlite.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.login.login.shop_sqlite.Dto.UserResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.List;

public class UserAdapter extends ArrayAdapter<UserResponseDto> {

    private final List<UserResponseDto> users;
    private final OnUserActionListener listener; // Interface để xử lý sự kiện click nút

    // Interface để gửi sự kiện click ra ngoài Activity
    public interface OnUserActionListener {
        void onEditClick(UserResponseDto user);
        void onDeleteClick(UserResponseDto user);
    }

    public UserAdapter(Context context, List<UserResponseDto> users, OnUserActionListener listener) {
        super(context, 0, users);
        this.users = users;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sale_item_user, parent, false);
        }

        UserResponseDto currentUser = getItem(position);

        TextView tvUserName = convertView.findViewById(R.id.tvUserName);
        TextView tvUserEmail = convertView.findViewById(R.id.tvUserEmail);
        TextView tvUserPhone = convertView.findViewById(R.id.tvUserPhone);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        if (currentUser != null) {
            tvUserName.setText(currentUser.getUserName() != null ? currentUser.getUserName() : "N/A");
            tvUserEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A");
            tvUserPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "N/A");

            // Xử lý sự kiện click cho nút Edit
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(currentUser);
                }
            });

            // Xử lý sự kiện click cho nút Delete
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(currentUser);
                }
            });
        }

        return convertView;
    }
}