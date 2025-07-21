package com.example.login.login.shop_sqlite.Adapter;

<<<<<<< HEAD
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.login.login.shop_sqlite.Models.Product;
import com.example.login.login.shop_sqlite.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PRODUCT = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<Product> products;
    private Context context;
    private OnProductClickListener listener;
    private boolean isLoading = false;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }
    
    public void setLoading(boolean loading) {
        this.isLoading = loading;
        if (loading) {
            notifyItemInserted(products.size());
        } else {
            notifyItemRemoved(products.size());
=======
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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductResponseDto> productList;
    private OnItemActionListener listener; // Khai báo listener

    // Interface để truyền sự kiện về Activity/Fragment
    public interface OnItemActionListener {
        void onEditClick(int productId);
        void onDeleteClick(int productId);
    }

    // Cập nhật constructor để nhận listener
    public ProductAdapter(List<ProductResponseDto> productList, OnItemActionListener listener) {
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
>>>>>>> hmthmt
        }
    }

    @NonNull
    @Override
<<<<<<< HEAD
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder) {
            Product product = products.get(position);
            ((ProductViewHolder) holder).bind(product);
        }
        // LoadingViewHolder doesn't need binding
    }

    @Override
    public int getItemViewType(int position) {
        if (position == products.size() && isLoading) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_PRODUCT;
=======
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
>>>>>>> hmthmt
    }

    @Override
    public int getItemCount() {
<<<<<<< HEAD
        return products != null ? products.size() + (isLoading ? 1 : 0) : 0;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productDescription;
        private TextView productPrice;
        private TextView brandBadge;
        private TextView categoryBadge;
        // private Button addToCartButton; // Đã loại bỏ nút này khỏi layout

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            brandBadge = itemView.findViewById(R.id.brandBadge);
            categoryBadge = itemView.findViewById(R.id.categoryBadge);
            // addToCartButton = itemView.findViewById(R.id.addToCartButton); // Đã loại bỏ nút này khỏi layout


            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    // Mở ProductDetailActivity khi click vào sản phẩm
                    Product product = products.get(position);
                    android.content.Intent intent = new android.content.Intent(context, com.example.login.login.shop_sqlite.Activity.ProductDetailActivity.class);
                    intent.putExtra("product_id", product.getProductId());
                    intent.putExtra("product_name", product.getName());
                    intent.putExtra("product_price", product.getBasePrice());
                    intent.putExtra("product_description", product.getDescription());
                    intent.putExtra("product_brand", product.getBrand());
                    intent.putExtra("product_category", product.getCategoryName());
                    intent.putExtra("product_image", product.getImageUrl());
                    context.startActivity(intent);
                }
            });

            // Xoá hoặc comment toàn bộ logic/thao tác thêm vào giỏ hàng ở ProductList (nút addToCartButton, listener, v.v.)
        }

        public void bind(Product product) {
            // Set product name
            productName.setText(product.getName());

            // Set product description
            if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                productDescription.setText(product.getDescription());
                productDescription.setVisibility(View.VISIBLE);
            } else {
                productDescription.setVisibility(View.GONE);
            }

            // Set product price (dùng formatPrice thay vì getFormattedPrice)
            productPrice.setText(formatPrice(product.getBasePrice()));

            // Set brand badge
            if (product.getBrand() != null && !product.getBrand().isEmpty()) {
                brandBadge.setText(product.getBrand());
                brandBadge.setVisibility(View.VISIBLE);
            } else {
                brandBadge.setVisibility(View.GONE);
            }

            // Set category badge
            if (product.getCategoryName() != null && !product.getCategoryName().isEmpty()) {
                categoryBadge.setText(product.getCategoryName());
                categoryBadge.setVisibility(View.VISIBLE);
            } else {
                categoryBadge.setVisibility(View.GONE);
            }

            // Load product image
            loadProductImage(product);
        }

        private void loadProductImage(Product product) {
            String imageUrl = product.getImageUrl();
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Load image using Glide
                Glide.with(context)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_err_image_layy)
                    .error(R.drawable.ic_err_image_layy)
                    .into(productImage);
            } else {
                // Set placeholder image
                productImage.setImageResource(R.drawable.ic_err_image_layy);
            }
        }
    }
    
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // Thêm hàm formatPrice để hiển thị giá dạng $50k, $1.2M
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
} 
=======
        return productList.size();
    }
}
>>>>>>> hmthmt
