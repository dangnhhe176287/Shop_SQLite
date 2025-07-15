package com.example.login.login.shop_sqlite.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.login.shop_sqlite.Models.OrderView;
import com.example.login.login.shop_sqlite.R;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
    private List<OrderView> orders;
    public OrderListAdapter(List<OrderView> orders) {
        this.orders = orders;
    }
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderView order = orders.get(position);
        holder.tvOrderId.setText("Mã đơn: " + order.getOrderId());
        holder.tvTotal.setText("Tổng tiền: " + formatPrice(order.getAmountDue()));
        holder.tvDate.setText("Ngày: " + (order.getCreatedAt() != null ? order.getCreatedAt().toString() : ""));
        holder.tvAddress.setText("Địa chỉ: " + order.getAddress());
    }
    @Override
    public int getItemCount() {
        return orders.size();
    }
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvTotal, tvDate, tvAddress;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }
    private String formatPrice(double price) {
        if (price >= 1_000_000) {
            return String.format("%.2fM VNĐ", price / 1_000_000);
        } else if (price >= 1_000) {
            if (price % 1000 == 0) {
                return String.format("%.0fk VNĐ", price / 1000);
            } else {
                return String.format("%.2fk VNĐ", price / 1000);
            }
        } else {
            return String.format("%.0f VNĐ", price);
        }
    }
} 