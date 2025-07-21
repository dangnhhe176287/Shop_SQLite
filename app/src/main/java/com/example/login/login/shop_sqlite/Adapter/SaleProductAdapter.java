package com.example.login.login.shop_sqlite.Adapter;

import android.view.*;
import android.widget.Button; // Thêm import này
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Dto.ProductResponseDto;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson; // Thêm import này
import com.google.gson.reflect.TypeToken; // Thêm import này

import java.lang.reflect.Type; // Thêm import này
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map; // Thêm import này

public class SaleProductAdapter extends RecyclerView.Adapter<SaleProductAdapter.ProductViewHolder> {

    private List<ProductResponseDto> productList;
    private OnItemActionListener listener; // Khai báo listener

    // Interface để truyền sự kiện về Activity/Fragment
    public interface OnItemActionListener {
        void onEditClick(int productId);
        void onDeleteClick(int productId);
    }

    // Cập nhật constructor để nhận listener
    public SaleProductAdapter(List<ProductResponseDto> productList, OnItemActionListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductId, txtName, txtDesc, txtCategoryId, txtBrand, txtPrice, txtAttributes, txtStatus, txtIsDelete, txtCreatedAt, txtUpdatedAt;
        Button btnEditProduct, btnDeleteProduct; // Khai báo các nút

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
            btnEditProduct = itemView.findViewById(R.id.btnEditProduct); // Ánh xạ nút Edit
            btnDeleteProduct = itemView.findViewById(R.id.btnDeleteProduct); // Ánh xạ nút Delete
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponseDto product = productList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        holder.txtProductId.setText("ID: " + product.getProductId());
        holder.txtName.setText("Name: " + product.getName());
        holder.txtDesc.setText("Description: " + product.getDescription());
        holder.txtCategoryId.setText("Category ID: " + (product.getProductCategoryId() != null ? product.getProductCategoryId() : "N/A"));
        holder.txtBrand.setText("Brand: " + product.getBrand());
        holder.txtPrice.setText(String.format(Locale.getDefault(), "Price: ₫%.2f", product.getBasePrice())); // Định dạng giá tốt hơn
        holder.txtStatus.setText("Status: " + (product.getStatus() != null ? product.getStatus() : "N/A"));
        holder.txtIsDelete.setText("Is Deleted: " + (product.isDelete() ? "Yes" : "No")); // Hiển thị Yes/No

        // Xử lý AvailableAttributes JSON
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


        // Xử lý CreatedAt
        if (product.getCreatedAt() != null) {
            try {
                holder.txtCreatedAt.setText("Created At: " + dateFormat.format(product.getCreatedAt()));
            } catch (IllegalArgumentException e) {
                holder.txtCreatedAt.setText("Created At: Invalid date");
            }
        } else {
            holder.txtCreatedAt.setText("Created At: N/A");
        }

        // Xử lý UpdatedAt
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
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}