package com.example.login.login.shop_sqlite.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Models.SaleOrder;
import com.example.login.login.shop_sqlite.R;

import java.util.List;
import java.util.Locale;

public class SaleOrderAdapter extends RecyclerView.Adapter<SaleOrderAdapter.OrderViewHolder> {

    private List<SaleOrder> saleOrderList;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEditClick(int orderId);
        void onDeleteClick(int orderId);
        void onDetailClick(int orderId); // Added for detail view
    }

    public SaleOrderAdapter(List<SaleOrder> saleOrderList, OnItemActionListener listener) {
        this.saleOrderList = saleOrderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        SaleOrder saleOrder = saleOrderList.get(position);

        holder.tvOrderId.setText("Mã đơn: " + saleOrder.getOrderId());
        holder.tvCustomerId.setText("ID Khách hàng: " + (saleOrder.getCustomerId() != null ? saleOrder.getCustomerId() : "N/A"));
        holder.tvTotalQuantity.setText("Tổng SP: " + (saleOrder.getTotalQuantity() != null ? saleOrder.getTotalQuantity() : 0));
        holder.tvAmountDue.setText(String.format(Locale.getDefault(), "Tổng tiền: %.2f VNĐ",
                (saleOrder.getAmountDue() != null ? saleOrder.getAmountDue() : new java.math.BigDecimal("0.0"))));
        holder.tvOrderStatus.setText("Trạng thái: " + (saleOrder.getOrderStatusId() != null ? getStatusName(saleOrder.getOrderStatusId()) : "N/A"));

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(saleOrder.getOrderId());
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(saleOrder.getOrderId());
            }
        });

        // Set OnClickListener for the detail eye icon
        holder.ivDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClick(saleOrder.getOrderId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return saleOrderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerId, tvTotalQuantity, tvAmountDue, tvOrderStatus;
        Button btnEdit, btnDelete;
        ImageView ivDetail; // Declare ImageView for detail icon

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerId = itemView.findViewById(R.id.tvCustomerId);
            tvTotalQuantity = itemView.findViewById(R.id.tvTotalQuantity);
            tvAmountDue = itemView.findViewById(R.id.tvAmountDue);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnEdit = itemView.findViewById(R.id.btnEditOrder);
            btnDelete = itemView.findViewById(R.id.btnDeleteOrder);
            ivDetail = itemView.findViewById(R.id.ivDetail); // Initialize ImageView
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