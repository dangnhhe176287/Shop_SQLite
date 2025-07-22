package com.example.login.login.shop_sqlite.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Activity.OrderDetailActivity;
import com.example.login.login.shop_sqlite.Models.OrderView;
import com.example.login.login.shop_sqlite.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
    private List<OrderView> orders;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat outputFormat;
    
    public OrderListAdapter(List<OrderView> orders) {
        this.orders = orders;
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault());
        this.outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_item_order, parent, false);
        return new OrderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderView order = orders.get(position);
        holder.tvOrderId.setText("Mã đơn: " + order.getOrderId());
        holder.tvTotal.setText("Tổng tiền: " + formatPrice(order.getAmountDue()));
        
        // Hiển thị trạng thái đơn hàng
        String statusText = order.getOrderStatusTitle() != null ? order.getOrderStatusTitle() : "Đang xử lý";
        holder.tvOrderStatus.setText(statusText);
        
        // Đặt màu cho trạng thái
        int statusColor = getStatusColor(order.getOrderStatusId());
        holder.tvOrderStatus.setBackgroundColor(statusColor);
        
        // Format thời gian từ String
        String dateText = "Ngày: ";
        if (order.getCreatedAt() != null && !order.getCreatedAt().isEmpty()) {
            try {
                Date date = inputFormat.parse(order.getCreatedAt());
                dateText += outputFormat.format(date);
            } catch (ParseException e) {
                dateText += order.getCreatedAt(); // Fallback to raw string
            }
        } else {
            dateText += "N/A";
        }
        holder.tvDate.setText(dateText);
        
        holder.tvAddress.setText("Địa chỉ: " + (order.getShippingAddress() != null ? order.getShippingAddress() : "N/A"));
        
        // Add click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
            intent.putExtra("order_id", order.getOrderId());
            v.getContext().startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return orders.size();
    }
    
    private String formatPrice(double price) {
        if (price >= 1_000_000) {
            return String.format("$%.2fM", price / 1_000_000);
        } else if (price >= 1_000) {
            if (price % 1000 == 0) {
                return String.format("$%.0fk", price / 1000);
            } else {
                return String.format("$%.2fk", price / 1000);
            }
        } else {
            return String.format("$%.2f", price);
        }
    }
    
    private int getStatusColor(int statusId) {
        switch (statusId) {
            case 1: // Pending
                return 0xFFE91E63; // Pink
            case 2: // Processing
                return 0xFFFF9800; // Orange
            case 3: // Shipped
                return 0xFF2196F3; // Blue
            case 4: // Delivered
                return 0xFF4CAF50; // Green
            case 5: // Cancelled
                return 0xFFF44336; // Red
            default:
                return 0xFFE91E63; // Default Pink
        }
    }
    
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvTotal, tvDate, tvAddress, tvOrderStatus;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvTotal = itemView.findViewById(R.id.tvTotalQuantity);
            tvDate = itemView.findViewById(R.id.tvDateOfBirth);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
} 