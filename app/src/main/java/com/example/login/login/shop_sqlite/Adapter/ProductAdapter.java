package com.example.login.login.shop_sqlite.Adapter;

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
        }
    }

    @NonNull
    @Override
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
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() + (isLoading ? 1 : 0) : 0;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productDescription;
        private TextView productPrice;
        private TextView brandBadge;
        private TextView categoryBadge;
        private Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            brandBadge = itemView.findViewById(R.id.brandBadge);
            categoryBadge = itemView.findViewById(R.id.categoryBadge);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);


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

            addToCartButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Product product = products.get(position);
                    // Lấy userId hiện tại từ SharedPreferences hoặc session
                    android.content.SharedPreferences prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
                    int userId = prefs.getInt("current_user_id", 0);
                    if (userId == 0) {
                        Toast.makeText(context, "Bạn cần đăng nhập trước khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    com.example.login.login.shop_sqlite.Models.CartItemDto item = new com.example.login.login.shop_sqlite.Models.CartItemDto(product.getProductId(), 1);
                    com.example.login.login.shop_sqlite.Api.ApiService apiService = com.example.login.login.shop_sqlite.Api.ApiClient.getClient().create(com.example.login.login.shop_sqlite.Api.ApiService.class);
                    apiService.addToCart(userId, item).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        }
                    });
                    listener.onAddToCartClick(product);
                }
            });
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

    // Thêm hàm formatPrice để hiển thị giá dạng 50k VNĐ, 1.2M VNĐ
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