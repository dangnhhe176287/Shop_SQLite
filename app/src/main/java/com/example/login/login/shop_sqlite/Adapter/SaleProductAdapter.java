// com.example.login.login.shop_sqlite.Adapter.ProductAdapter.java

package com.example.login.login.shop_sqlite.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView; // <--- THÊM IMPORT NÀY
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Dto.SaleProductResponseDto;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SaleProductAdapter extends RecyclerView.Adapter<SaleProductAdapter.ProductViewHolder> {

    private List<SaleProductResponseDto> productList;
    private OnItemActionListener listener;

    // Interface để truyền sự kiện về Activity/Fragment (ĐÃ CẬP NHẬT)
    public interface OnItemActionListener {
        void onEditClick(int productId);
        void onDeleteClick(int productId);
        void onDetailClick(int productId); // <--- THÊM PHƯƠNG THỨC NÀY
    }

    // Cập nhật constructor để nhận listener
    public SaleProductAdapter(List<SaleProductResponseDto> productList, OnItemActionListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductId, txtName, txtDesc, txtCategoryId, txtBrand, txtPrice, txtAttributes, txtStatus, txtIsDelete, txtCreatedAt, txtUpdatedAt;
        Button btnEditProduct, btnDeleteProduct;
        ImageView ivDetail; // <--- THÊM KHAI BÁO ImageView CHO BIỂU TƯỢNG MẮT

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtProductId = itemView.findViewById(R.id.txtProductId);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtDesc = itemView.findViewById(R.id.txtProductDesc);
            txtCategoryId = itemView.findViewById(R.id.txtProductCategoryId);
            txtBrand = itemView.findViewById(R.id.txtBrand);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
            txtAttributes = itemView.findViewById(R.id.txtAvailableAttributes);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtIsDelete = itemView.findViewById(R.id.txtIsDelete);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtUpdatedAt = itemView.findViewById(R.id.txtUpdatedAt);
            btnEditProduct = itemView.findViewById(R.id.btnEditProduct);
            btnDeleteProduct = itemView.findViewById(R.id.btnDeleteProduct);
            ivDetail = itemView.findViewById(R.id.ivDetail); // <--- ÁNH XẠ ImageView
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sale_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        SaleProductResponseDto product = productList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        holder.txtProductId.setText("ID: " + product.getProductId());
        holder.txtName.setText("Name: " + product.getName());
        holder.txtDesc.setText("Description: " + product.getDescription());
        holder.txtCategoryId.setText("Category ID: " + (product.getProductCategoryId() != null ? product.getProductCategoryId() : "N/A"));
        holder.txtBrand.setText("Brand: " + product.getBrand());
        holder.txtPrice.setText(String.format(Locale.getDefault(), "Price: ₫%.2f", product.getBasePrice()));
        holder.txtStatus.setText("Status: " + (product.getStatus() != null ? product.getStatus() : "N/A"));
        holder.txtIsDelete.setText("Is Deleted: " + (product.isDelete() ? "Yes" : "No"));

        if (product.getAvailableAttributes() != null && !product.getAvailableAttributes().isEmpty()) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
                Map<String, List<String>> attributesMap = gson.fromJson(product.getAvailableAttributes(), type);
                StringBuilder sb = new StringBuilder("Attributes: ");
                if (attributesMap.containsKey("size")) {
                    sb.append("Size: ").append(android.text.TextUtils.join(", ", attributesMap.get("size"))).append("; ");
                }
                if (attributesMap.containsKey("color")) {
                    sb.append("Color: ").append(android.text.TextUtils.join(", ", attributesMap.get("color"))).append("; ");
                }
                holder.txtAttributes.setText(sb.toString().trim());
            } catch (Exception e) {
                holder.txtAttributes.setText("Attributes: Parsing error");
                e.printStackTrace();
            }
        } else {
            holder.txtAttributes.setText("Attributes: N/A");
        }

        if (product.getCreatedAt() != null) {
            try {
                holder.txtCreatedAt.setText("Created At: " + dateFormat.format(product.getCreatedAt()));
            } catch (IllegalArgumentException e) {
                holder.txtCreatedAt.setText("Created At: Invalid date");
            }
        } else {
            holder.txtCreatedAt.setText("Created At: N/A");
        }

        if (product.getUpdatedAt() != null) {
            try {
                holder.txtUpdatedAt.setText("Updated At: " + dateFormat.format(product.getUpdatedAt()));
            } catch (IllegalArgumentException e) {
                holder.txtUpdatedAt.setText("Updated At: Invalid date");
            }
        } else {
            holder.txtUpdatedAt.setText("Updated At: N/A");
        }

        // --- Thiết lập OnClickListener cho các nút ---
        holder.btnEditProduct.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(product.getProductId());
            }
        });

        holder.btnDeleteProduct.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(product.getProductId());
            }
        });

        // <--- THIẾT LẬP ONCLICKLISTENER CHO BIỂU TƯỢNG MẮT (XEM CHI TIẾT)
        holder.ivDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClick(product.getProductId());
            }
        });
        // THIẾT LẬP ONCLICKLISTENER CHO BIỂU TƯỢNG MẮT (XEM CHI TIẾT) --->
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}