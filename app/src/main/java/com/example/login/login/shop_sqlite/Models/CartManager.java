package com.example.login.login.shop_sqlite.Models;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREFS_NAME = "cart_prefs";
    private static final String CART_KEY = "cart_items";
    private static CartManager instance;
    private List<CartItem> cartItems = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(CartItem item, Context context) {
        loadCart(context);
        for (CartItem ci : cartItems) {
            if (ci.getProductId() == item.getProductId()) {
                ci.setQuantity(ci.getQuantity() + item.getQuantity());
                saveCart(context);
                return;
            }
        }
        cartItems.add(item);
        saveCart(context);
    }

    public List<CartItem> getCartItems(Context context) {
        loadCart(context);
        return cartItems;
    }

    private void saveCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString(CART_KEY, gson.toJson(cartItems));
        editor.apply();
    }

    private void loadCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CART_KEY, null);
        if (json != null) {
            cartItems = new Gson().fromJson(json, new TypeToken<List<CartItem>>(){}.getType());
        } else {
            cartItems = new ArrayList<>();
        }
    }
} 