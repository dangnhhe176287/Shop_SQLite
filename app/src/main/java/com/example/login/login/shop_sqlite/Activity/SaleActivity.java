package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.login.login.shop_sqlite.SaleFragment.OrderListFragment;
import com.example.login.login.shop_sqlite.SaleFragment.ProductCategoriesFragment;
import com.example.login.login.shop_sqlite.SaleFragment.ProductListFragment;
import com.example.login.login.shop_sqlite.SaleFragment.UserListFragment;
import com.example.login.login.shop_sqlite.R;
import com.google.android.material.navigation.NavigationView;

public class SaleActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_sale);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Load Fragment mặc định khi Activity được tạo lần đầu
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProductListFragment()) // Mặc định hiển thị Sản phẩm
                    .commit();
            navigationView.setCheckedItem(R.id.nav_products);
            setTitle("Sản phẩm");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "";

        int id = item.getItemId();

        if (id == R.id.nav_products) {
            selectedFragment = new ProductListFragment();
            title = "Sản phẩm";
        } else if (id == R.id.nav_orders) {
            selectedFragment = new OrderListFragment();
            title = "Đơn hàng";
        } else if (id == R.id.nav_users) {
            selectedFragment = new UserListFragment();
            title = "Người dùng";
        } else if (id == R.id.nav_categories) {
            selectedFragment = new ProductCategoriesFragment();
            title = "Danh mục sản phẩm";
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            setActionBarTitle(title);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Khi chỉ hiển thị các Fragment chính từ Drawer, không cần Back Stack phức tạp.
            // Nếu Drawer không mở, và không có Fragment nào trong back stack (tức là đang ở màn hình chính),
            // thì thoát Activity.
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    // --- Các phương thức liên quan đến Create/Edit User đã được loại bỏ ở đây ---
    // Vì SaleActivity giờ đây chỉ chịu trách nhiệm điều hướng các Fragment chính.
    // Logic thêm/sửa/xóa user sẽ được xử lý hoàn toàn trong UserListFragment
    // và các Fragment liên quan đến User mà không cần SaleActivity trung gian.
    // Nếu bạn muốn tích hợp lại chúng, bạn sẽ cần thêm lại các interface và phương thức.
}