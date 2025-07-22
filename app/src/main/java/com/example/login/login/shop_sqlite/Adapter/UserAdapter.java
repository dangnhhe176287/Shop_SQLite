// com.example.login.login.shop_sqlite.Adapter.UserAdapter.java

package com.example.login.login.shop_sqlite.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView; // <--- THÊM IMPORT NÀY
import android.widget.TextView;

import com.example.login.login.shop_sqlite.Dto.UserResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.List;

public class UserAdapter extends ArrayAdapter<UserResponseDto> {

    private final List<UserResponseDto> users;
    private final OnUserActionListener listener;

    // Interface để gửi sự kiện click ra ngoài Activity (ĐÃ CẬP NHẬT)
    public interface OnUserActionListener {
        void onEditClick(UserResponseDto user);
        void onDeleteClick(UserResponseDto user);
        void onDetailClick(UserResponseDto user); // <--- THÊM PHƯƠNG THỨC NÀY
    }

    public UserAdapter(Context context, List<UserResponseDto> users, OnUserActionListener listener) {
        super(context, 0, users);
        this.users = users;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder; // Sử dụng ViewHolder pattern
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserResponseDto currentUser = getItem(position);

        if (currentUser != null) {
            holder.tvUserName.setText(currentUser.getUserName() != null ? currentUser.getUserName() : "N/A");
            holder.tvUserEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A");
            holder.tvUserPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "N/A");

            // Xử lý sự kiện click cho nút Edit
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(currentUser);
                }
            });

            // Xử lý sự kiện click cho nút Delete
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(currentUser);
                }
            });

            // <--- THÊM ONCLICKLISTENER CHO BIỂU TƯỢNG MẮT (XEM CHI TIẾT)
            holder.ivDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDetailClick(currentUser);
                }
            });
            // THÊM ONCLICKLISTENER CHO BIỂU TƯỢNG MẮT (XEM CHI TIẾT) --->
        }

        return convertView;
    }

    // Sử dụng ViewHolder để tối ưu hiệu suất của ListView/ArrayAdapter
    static class ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserPhone;
        Button btnEdit, btnDelete;
        ImageView ivDetail; // <--- KHAI BÁO ImageView CHO BIỂU TƯỢNG MẮT

        ViewHolder(View view) {
            tvUserName = view.findViewById(R.id.tvUserName);
            tvUserEmail = view.findViewById(R.id.tvUserEmail);
            tvUserPhone = view.findViewById(R.id.tvUserPhone);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
            ivDetail = view.findViewById(R.id.ivDetail); // <--- ÁNH XẠ ImageView
        }
    }
}