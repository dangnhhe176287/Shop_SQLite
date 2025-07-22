package com.example.login.login.shop_sqlite.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Models.OrderView;
import com.example.login.login.shop_sqlite.R;

import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    private static final String TAG = "OrderDetailAdapter";
    private List<OrderView.OrderDetailView> orderDetails;
    
    public OrderDetailAdapter(List<OrderView.OrderDetailView> orderDetails) {
        this.orderDetails = orderDetails;
    }
    
    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderView.OrderDetailView detail = orderDetails.get(position);
        
        holder.tvProductName.setText(detail.getProductName());
        holder.tvPrice.setText("Giá: " + formatPrice(detail.getPrice()));
        holder.tvQuantity.setText("Số lượng: " + detail.getQuantity());
        holder.tvSubtotal.setText("Tổng: " + formatPrice(detail.getPrice() * detail.getQuantity()));
        
        // Debug variant attributes
        Log.d(TAG, "Product: " + detail.getProductName() + ", VariantAttributes: " + detail.getVariantAttributes());
        
        // Hiển thị variant attributes nếu có
        if (detail.getVariantAttributes() != null && !detail.getVariantAttributes().isEmpty()) {
            holder.tvVariantAttributes.setText("Thuộc tính: " + detail.getVariantAttributes());
            holder.tvVariantAttributes.setVisibility(View.VISIBLE);
            Log.d(TAG, "Showing variant attributes for: " + detail.getProductName());
        } else {
            holder.tvVariantAttributes.setVisibility(View.GONE);
            Log.d(TAG, "Hiding variant attributes for: " + detail.getProductName());
        }
    }
    
    @Override
    public int getItemCount() {
        return orderDetails.size();
    }
    
    private String formatPrice(double price) {
        if (price >= 1_000_000) {
            return String.format(Locale.getDefault(), "$%.2fM", price / 1_000_000);
        } else if (price >= 1_000) {
            if (price % 1000 == 0) {
                return String.format(Locale.getDefault(), "$%.0fk", price / 1000);
            } else {
                return String.format(Locale.getDefault(), "$%.2fk", price / 1000);
            }
        } else {
            return String.format(Locale.getDefault(), "$%.2f", price);
        }
    }
    
    static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, tvQuantity, tvSubtotal, tvVariantAttributes;
        
        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            tvVariantAttributes = itemView.findViewById(R.id.tvVariantAttributes);
        }
    }
} 