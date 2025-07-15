package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get user info from intent
        String userName = getIntent().getStringExtra("userName");
        String token = getIntent().getStringExtra("token");

        TextView welcomeText = findViewById(R.id.welcomeText);
        Button btnViewProducts = findViewById(R.id.btnViewProducts);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnViewOrders = findViewById(R.id.btnViewOrders);
        FloatingActionButton fabMenu = findViewById(R.id.fabMenu);

        if (userName != null) {
            welcomeText.setText("Welcome, " + userName + "!");
        }

        btnViewProducts.setOnClickListener(v -> {
            // TODO: Navigate to ProductListActivity when it's recreated
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrderListActivity.class);
            startActivity(intent);
        });

        fabMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, fabMenu);
            popup.getMenu().add("Xem đơn hàng");
            // Có thể add thêm các chức năng khác ở đây
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Xem đơn hàng")) {
                    Intent intent = new Intent(MainActivity.this, OrderListActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }
} 