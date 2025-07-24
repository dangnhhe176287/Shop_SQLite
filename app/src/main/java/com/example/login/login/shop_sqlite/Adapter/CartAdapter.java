package com.example.login.login.shop_sqlite.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Models.CartItemDto;
import com.example.login.login.shop_sqlite.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItemDto> cartItems;

    public CartAdapter(List<CartItemDto> cartItems) {
        this.cartItems = (cartItems != null) ? cartItems : new java.util.ArrayList<>();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemDto item = cartItems.get(position);
        // Hiển thị tên sản phẩm nếu có, nếu không thì fallback sang ID
        String name = item.getProductName();
        if (name != null && !name.isEmpty()) {
            holder.name.setText(name);
        } else {
            holder.name.setText("ID: " + item.getProductId());
        }
        // Hiển thị giá nếu có
        if (item.getPrice() != null) {
            holder.price.setText(formatPrice(item.getPrice()));
        } else {
            holder.price.setText("");
        }
        // Hiển thị số lượng (chỉ số, không có ký tự thừa)
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        // Hiển thị thành tiền
        double total = (item.getPrice() != null ? item.getPrice() : 0) * item.getQuantity();
        holder.totalPrice.setText("Thành tiền: " + formatPrice(total));
        // Hiển thị ảnh sản phẩm nếu có imageUrl
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.image.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_err_image_layy)
                .error(R.drawable.ic_err_image_layy)
                .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_err_image_layy);
        }
        // Không cần setText cho ImageButton nữa
        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            updateCartItem(holder, item, newQuantity);
        });
        holder.btnDecrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity > 0) {
                updateCartItem(holder, item, newQuantity);
            }
        });
        holder.btnRemove.setOnClickListener(v -> {
            removeCartItem(holder, item);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, quantity, totalPrice;
        ImageButton btnIncrease, btnDecrease;
        ImageButton btnRemove;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cartItemImage);
            name = itemView.findViewById(R.id.cartItemName);
            price = itemView.findViewById(R.id.cartItemPrice);
            quantity = itemView.findViewById(R.id.cartItemQuantity);
            totalPrice = itemView.findViewById(R.id.cartItemTotalPrice);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }

    public String formatPrice(double price) {
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

    public interface OnCartChangedListener {
        void onCartChanged();
    }
    private OnCartChangedListener cartChangedListener;
    public void setOnCartChangedListener(OnCartChangedListener listener) {
        this.cartChangedListener = listener;
    }

    private void updateCartItem(CartViewHolder holder, CartItemDto item, int newQuantity) {
        SharedPreferences prefs = holder.itemView.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("current_user_id", 0);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        CartItemDto updateItem = new CartItemDto(item.getProductId(), newQuantity);
        apiService.updateCartItem(userId, updateItem).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                item.setQuantity(newQuantity);
                notifyDataSetChanged();
                if (cartChangedListener != null) cartChangedListener.onCartChanged();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "Lỗi cập nhật số lượng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeCartItem(CartViewHolder holder, CartItemDto item) {
        SharedPreferences prefs = holder.itemView.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("current_user_id", 0);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.removeFromCart(userId, item.getProductId(), item.getVariantId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                cartItems.remove(item);
                notifyDataSetChanged();
                if (cartChangedListener != null) cartChangedListener.onCartChanged();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "Lỗi xóa sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 