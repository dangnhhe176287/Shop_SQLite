package com.example.login.login.shop_sqlite.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Dto.OrderDetailResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.List;
import java.util.Locale;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.OrderDetailItemViewHolder> {

    private List<OrderDetailResponseDto> detailList;

    public OrderDetailItemAdapter(List<OrderDetailResponseDto> detailList) {
        this.detailList = detailList;
    }

    @NonNull
    @Override
    public OrderDetailItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_product, parent, false); // Sẽ tạo layout này
        return new OrderDetailItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailItemViewHolder holder, int position) {
        OrderDetailResponseDto detail = detailList.get(position);
        holder.tvProductName.setText("Sản phẩm: " + detail.getProductName());
        holder.tvProductId.setText("ID SP: " + (detail.getProductId() != null ? detail.getProductId() : "N/A"));
        holder.tvVariantId.setText("Biến thể: " + (detail.getVariantId() != null ? detail.getVariantId() : "N/A"));
        holder.tvQuantity.setText("Số lượng: " + detail.getQuantity());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Giá: %.2f VNĐ", (detail.getPrice() != null ? detail.getPrice() : 0.0)));
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    public static class OrderDetailItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductId, tvVariantId, tvQuantity, tvPrice;

        public OrderDetailItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvVariantId = itemView.findViewById(R.id.tvVariantId);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}