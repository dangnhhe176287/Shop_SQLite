package com.example.login.login.shop_sqlite.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Models.Order;
import com.example.login.login.shop_sqlite.R;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEditClick(int orderId);
        void onDeleteClick(int orderId);
    }

    public OrderAdapter(List<Order> orderList, OnItemActionListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Mã đơn: " + order.getOrderId());

        holder.tvCustomerId.setText("ID Khách hàng: " + (order.getCustomerId() != null ? order.getCustomerId() : "N/A"));

        holder.tvTotalQuantity.setText("Tổng SP: " + (order.getTotalQuantity() != null ? order.getTotalQuantity() : 0));

        holder.tvAmountDue.setText(String.format(Locale.getDefault(), "Tổng tiền: %.2f VNĐ",
                (order.getAmountDue() != null ? order.getAmountDue() : new java.math.BigDecimal("0.0"))));

        holder.tvOrderStatus.setText("Trạng thái: " + (order.getOrderStatusId() != null ? getStatusName(order.getOrderStatusId()) : "N/A"));

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(order.getOrderId());
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(order.getOrderId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerId, tvTotalQuantity, tvAmountDue, tvOrderStatus;
        Button btnEdit, btnDelete;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerId = itemView.findViewById(R.id.tvCustomerId);
            tvTotalQuantity = itemView.findViewById(R.id.tvTotalQuantity);
            tvAmountDue = itemView.findViewById(R.id.tvAmountDue);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnEdit = itemView.findViewById(R.id.btnEditOrder);
            btnDelete = itemView.findViewById(R.id.btnDeleteOrder);
        }
    }

    private String getStatusName(int statusId) {
        switch (statusId) {
            case 1: return "Chờ xử lý";
            case 2: return "Đang xử lý";
            case 3: return "Đã giao hàng";
            case 4: return "Đã hủy";
            default: return "Không xác định";
        }
    }
}