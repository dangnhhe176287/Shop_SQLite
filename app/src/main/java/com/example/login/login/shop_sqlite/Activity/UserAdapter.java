package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.login.shop_sqlite.Models.UserDto;
import com.example.login.login.shop_sqlite.R;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<UserDto> userList;
    private final OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEdit(UserDto user);

        void onDelete(UserDto user);

        void onView(UserDto user);
    }

    public UserAdapter(List<UserDto> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDto user = userList.get(position);
        holder.tvUserName.setText(user.userName);
        holder.tvEmail.setText(user.email);
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
        holder.btnView.setOnClickListener(v -> listener.onView(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvEmail;
        ImageButton btnEdit, btnDelete, btnView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}