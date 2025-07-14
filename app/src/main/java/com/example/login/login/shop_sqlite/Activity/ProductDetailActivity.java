package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.login.login.shop_sqlite.R;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView productImage = findViewById(R.id.productImage);
        TextView productName = findViewById(R.id.productName);
        TextView productPrice = findViewById(R.id.productPrice);
        TextView productDescription = findViewById(R.id.productDescription);
        TextView productBrand = findViewById(R.id.productBrand);
        TextView productCategory = findViewById(R.id.productCategory);

        // Nhận dữ liệu từ Intent
        String name = getIntent().getStringExtra("product_name");
        double price = getIntent().getDoubleExtra("product_price", 0);
        String description = getIntent().getStringExtra("product_description");
        String brand = getIntent().getStringExtra("product_brand");
        String category = getIntent().getStringExtra("product_category");
        String image = getIntent().getStringExtra("product_image");

        productName.setText(name);
        productPrice.setText(formatPrice(price));
        productDescription.setText(description);
        productBrand.setText(brand);
        productCategory.setText(category);
        if (image != null && !image.isEmpty()) {
            Glide.with(this).load(image).placeholder(R.drawable.ic_err_image_layy).into(productImage);
        } else {
            productImage.setImageResource(R.drawable.ic_err_image_layy);
        }
    }

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