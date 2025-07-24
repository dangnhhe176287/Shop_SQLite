package com.example.login.login.shop_sqlite.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Dto.SaleOrderDetailResponseDto;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SaleOrderDetailItemAdapter extends RecyclerView.Adapter<SaleOrderDetailItemAdapter.OrderDetailItemViewHolder> {

    private List<SaleOrderDetailResponseDto> detailList;
    private Gson gson = new Gson();

    public SaleOrderDetailItemAdapter(List<SaleOrderDetailResponseDto> detailList) {
        this.detailList = detailList;
    }

    @NonNull
    @Override
    public OrderDetailItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_item_order_detail_product, parent, false);
        return new OrderDetailItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailItemViewHolder holder, int position) {
        SaleOrderDetailResponseDto detail = detailList.get(position);
        holder.tvProductName.setText("Sản phẩm: " + detail.getProductName());
        holder.tvProductId.setText("ID SP: " + (detail.getProductId() != null ? detail.getProductId() : "N/A"));
        holder.tvVariantId.setText("Biến thể: " + (detail.getVariantId() != null && !detail.getVariantId().isEmpty() ? detail.getVariantId() : "N/A"));
        holder.tvQuantity.setText("Số lượng: " + detail.getQuantity());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Giá: %.2f VNĐ", detail.getPrice()));

        String variantAttributesJson = detail.getVariantAttributes();
        if (variantAttributesJson != null && !variantAttributesJson.isEmpty()) {
            try {
                Type type = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> attrs = gson.fromJson(variantAttributesJson, type);

                StringBuilder attributesDisplay = new StringBuilder("Thuộc tính: ");
                boolean first = true;
                for (Map.Entry<String, String> entry : attrs.entrySet()) {
                    if (!first) {
                        attributesDisplay.append(", ");
                    }
                    attributesDisplay.append(entry.getKey()).append(": ").append(entry.getValue());
                    first = false;
                }
                holder.tvVariantAttributes.setText(attributesDisplay.toString());
                holder.tvVariantAttributes.setVisibility(View.VISIBLE);
            } catch (JsonSyntaxException e) {
                holder.tvVariantAttributes.setText("Thuộc tính: Lỗi định dạng JSON: " + variantAttributesJson);
                holder.tvVariantAttributes.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        } else {
            holder.tvVariantAttributes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    public static class OrderDetailItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductId, tvVariantId, tvQuantity, tvPrice, tvVariantAttributes; // THÊM tvVariantAttributes

        public OrderDetailItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvVariantId = itemView.findViewById(R.id.tvVariantId);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvVariantAttributes = itemView.findViewById(R.id.tvVariantAttributes);
        }
    }
}