package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.PopupMenu;

import com.example.login.login.shop_sqlite.Adapter.ProductAdapter;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Models.Product;
import com.example.login.login.shop_sqlite.Models.FilterData;
import com.example.login.login.shop_sqlite.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> allProducts;
    private List<Product> filteredProducts;
    
    private EditText searchEditText;
    private ImageButton filterButton;
    private ProgressBar loadingProgressBar;
    private LinearLayout emptyStateLayout;
    private LinearLayout errorStateLayout;
    private TextView errorMessageText;
    private TextView paginationInfoText;
    private LinearLayout activeFilterLayout;
    private TextView activeFilterText;
    private Button clearFilterButton;
    
    private ApiService apiService;
    private int currentPage = 1;
    private int pageSize = 10; // Changed to 10 products per page
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    
    // Filter data
    private FilterData currentFilter = new FilterData();
    private List<String> categories = new ArrayList<>();
    private List<String> brands = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        
        initializeViews();
        setupRecyclerView();
        setupSearch();
        setupApiService();
        loadProducts();

        // Thêm xử lý cho cartButton
        ImageButton cartButton = findViewById(R.id.cartButton);
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        FloatingActionButton fabMenu = findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ProductListActivity.this, fabMenu);
            popup.getMenu().add("Xem đơn hàng");
            // Có thể add thêm các chức năng khác ở đây
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Xem đơn hàng")) {
                    Intent intent = new Intent(ProductListActivity.this, OrderListActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void initializeViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        filterButton = findViewById(R.id.filterButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        errorStateLayout = findViewById(R.id.errorStateLayout);
        errorMessageText = findViewById(R.id.errorMessageText);
        paginationInfoText = findViewById(R.id.paginationInfoText);
        activeFilterLayout = findViewById(R.id.activeFilterLayout);
        activeFilterText = findViewById(R.id.activeFilterText);
        clearFilterButton = findViewById(R.id.clearFilterButton);
        
        // Initialize lists
        allProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, filteredProducts);
        productAdapter.setOnProductClickListener(this);
        
        // Use GridLayoutManager for better product display
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productsRecyclerView.setLayoutManager(layoutManager);
        productsRecyclerView.setAdapter(productAdapter);
        
        // Add scroll listener for pagination
        productsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                if (!isLoading && hasMoreData) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize) {
                        loadMoreProducts();
                    }
                }
            }
        });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterButton.setOnClickListener(v -> {
            showFilterDialog();
        });
        
        clearFilterButton.setOnClickListener(v -> {
            currentFilter.clear();
            updateActiveFilterIndicator();
            filterProducts(searchEditText.getText().toString());
        });
    }

    private void setupApiService() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void loadProducts() {
        if (isLoading) return;
        
        // Reset pagination for fresh load
        currentPage = 1;
        hasMoreData = true;
        allProducts.clear();
        
        isLoading = true;
        showLoading(true);
        hideError();
        hideEmpty();

        Call<List<Product>> call = apiService.getAllProducts(currentPage, pageSize);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                isLoading = false;
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> newProducts = response.body();
                    
                    // Check if we have more data
                    if (newProducts.size() < pageSize) {
                        hasMoreData = false;
                    }
                    
                    allProducts.addAll(newProducts);
                    filterProducts(searchEditText.getText().toString());
                    
                    if (allProducts.isEmpty()) {
                        showEmpty();
                    } else {
                        updatePaginationInfo();
                    }
                    
                    // Increment page for next load
                    currentPage++;
                } else {
                    showError("Failed to load products. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                isLoading = false;
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }
    
    private void loadMoreProducts() {
        if (isLoading || !hasMoreData) return;
        
        isLoading = true;
        productAdapter.setLoading(true);
        
        Call<List<Product>> call = apiService.getAllProducts(currentPage, pageSize);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                isLoading = false;
                productAdapter.setLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> newProducts = response.body();
                    
                    // Check if we have more data
                    if (newProducts.size() < pageSize) {
                        hasMoreData = false;
                    }
                    
                    // Add new products to the list
                    int startPosition = allProducts.size();
                    allProducts.addAll(newProducts);
                    
                    // Update filtered products
                    filterProducts(searchEditText.getText().toString());
                    
                    // Notify adapter about new items
                    productAdapter.notifyItemRangeInserted(startPosition, newProducts.size());
                    
                    // Update pagination info
                    updatePaginationInfo();
                    
                    // Increment page for next load
                    currentPage++;
                } else {
                    // If failed, we might want to retry or show error
                    Toast.makeText(ProductListActivity.this, "Failed to load more products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                isLoading = false;
                productAdapter.setLoading(false);
                Toast.makeText(ProductListActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        
        for (Product product : allProducts) {
            boolean matchesSearch = true;
            boolean matchesFilter = true;
            
            // Apply search filter
            if (!query.isEmpty()) {
                String lowerQuery = query.toLowerCase();
                matchesSearch = product.getName().toLowerCase().contains(lowerQuery) ||
                    (product.getDescription() != null && product.getDescription().toLowerCase().contains(lowerQuery)) ||
                    (product.getBrand() != null && product.getBrand().toLowerCase().contains(lowerQuery)) ||
                    (product.getCategoryName() != null && product.getCategoryName().toLowerCase().contains(lowerQuery));
            }
            
            // Apply price filter
            if (currentFilter.hasPriceFilter()) {
                double productPrice = product.getBasePrice();
                matchesFilter = productPrice >= currentFilter.getMinPrice() && 
                               productPrice <= currentFilter.getMaxPrice();
            }
            
            // Apply category filter
            if (currentFilter.hasCategoryFilter() && matchesFilter) {
                matchesFilter = product.getCategoryName() != null && 
                               product.getCategoryName().equals(currentFilter.getCategory());
            }
            
            // Apply brand filter
            if (currentFilter.hasBrandFilter() && matchesFilter) {
                matchesFilter = product.getBrand() != null && 
                               product.getBrand().equals(currentFilter.getBrand());
            }
            
            // Add product if it matches both search and filter
            if (matchesSearch && matchesFilter) {
                filteredProducts.add(product);
            }
        }
        
        productAdapter.updateProducts(filteredProducts);
        
        if (filteredProducts.isEmpty() && !allProducts.isEmpty()) {
            showEmpty();
        } else {
            hideEmpty();
        }
        
        // Update pagination info after filtering
        updatePaginationInfo();
        updateActiveFilterIndicator();
    }

    private void showLoading(boolean show) {
        loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmpty() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        productsRecyclerView.setVisibility(View.GONE);
    }

    private void hideEmpty() {
        emptyStateLayout.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        errorMessageText.setText(message);
        errorStateLayout.setVisibility(View.VISIBLE);
        productsRecyclerView.setVisibility(View.GONE);
    }

    private void hideError() {
        errorStateLayout.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.VISIBLE);
    }
    
    private void updatePaginationInfo() {
        if (allProducts.size() > 0) {
            int currentPageDisplay = currentPage - 1; // Since we increment after loading
            String info = String.format("Page %d - Showing %d products (10 per page)", 
                currentPageDisplay, allProducts.size());
            paginationInfoText.setText(info);
            paginationInfoText.setVisibility(View.VISIBLE);
        } else {
            paginationInfoText.setVisibility(View.GONE);
        }
    }
    
    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        
        // Initialize views
        RangeSlider priceRangeSlider = dialogView.findViewById(R.id.priceRangeSlider);
        TextView minPriceText = dialogView.findViewById(R.id.minPriceText);
        TextView maxPriceText = dialogView.findViewById(R.id.maxPriceText);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        Spinner brandSpinner = dialogView.findViewById(R.id.brandSpinner);
        Button btnClearFilter = dialogView.findViewById(R.id.btnClearFilter);
        Button btnApplyFilter = dialogView.findViewById(R.id.btnApplyFilter);
        
        // Setup price range slider
        priceRangeSlider.setValueFrom(0f);
        priceRangeSlider.setValueTo(10000000f);
        priceRangeSlider.setValues((float) currentFilter.getMinPrice(), (float) currentFilter.getMaxPrice());
        
        // Update price text when slider changes
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            if (values.size() >= 2) {
                float minPrice = values.get(0);
                float maxPrice = values.get(1);
                minPriceText.setText(String.format("Min: %,.0f VNĐ", minPrice));
                maxPriceText.setText(String.format("Max: %,.0f VNĐ", maxPrice));
            }
        });
        
        // Setup category spinner
        setupCategorySpinner(categorySpinner);
        
        // Setup brand spinner
        setupBrandSpinner(brandSpinner);
        
        // Set current filter values
        if (currentFilter.hasCategoryFilter()) {
            int categoryPosition = categories.indexOf(currentFilter.getCategory());
            if (categoryPosition >= 0) {
                categorySpinner.setSelection(categoryPosition + 1); // +1 for "All Categories"
            }
        }
        
        if (currentFilter.hasBrandFilter()) {
            int brandPosition = brands.indexOf(currentFilter.getBrand());
            if (brandPosition >= 0) {
                brandSpinner.setSelection(brandPosition + 1); // +1 for "All Brands"
            }
        }
        
        // Clear filter button
        btnClearFilter.setOnClickListener(v -> {
            currentFilter.clear();
            priceRangeSlider.setValues(0f, 10000000f);
            categorySpinner.setSelection(0);
            brandSpinner.setSelection(0);
        });
        
        // Apply filter button
        btnApplyFilter.setOnClickListener(v -> {
            List<Float> priceValues = priceRangeSlider.getValues();
            if (priceValues.size() >= 2) {
                currentFilter.setMinPrice(priceValues.get(0));
                currentFilter.setMaxPrice(priceValues.get(1));
            }
            
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            if (!selectedCategory.equals("All Categories")) {
                currentFilter.setCategory(selectedCategory);
            } else {
                currentFilter.setCategory("");
            }
            
            String selectedBrand = brandSpinner.getSelectedItem().toString();
            if (!selectedBrand.equals("All Brands")) {
                currentFilter.setBrand(selectedBrand);
            } else {
                currentFilter.setBrand("");
            }
            
            currentFilter.setActive(true);
            
            // Reset pagination and reload products with filter
            resetPaginationAndReload();
            updateActiveFilterIndicator();
            
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private void setupCategorySpinner(Spinner spinner) {
        // Extract unique categories from products
        categories.clear();
        for (Product product : allProducts) {
            if (product.getCategoryName() != null && !product.getCategoryName().isEmpty()) {
                if (!categories.contains(product.getCategoryName())) {
                    categories.add(product.getCategoryName());
                }
            }
        }
        
        List<String> categoryOptions = new ArrayList<>();
        categoryOptions.add("All Categories");
        categoryOptions.addAll(categories);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, categoryOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    
    private void setupBrandSpinner(Spinner spinner) {
        // Extract unique brands from products
        brands.clear();
        for (Product product : allProducts) {
            if (product.getBrand() != null && !product.getBrand().isEmpty()) {
                if (!brands.contains(product.getBrand())) {
                    brands.add(product.getBrand());
                }
            }
        }
        
        List<String> brandOptions = new ArrayList<>();
        brandOptions.add("All Brands");
        brandOptions.addAll(brands);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, brandOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    
    private void resetPaginationAndReload() {
        currentPage = 1;
        hasMoreData = true;
        allProducts.clear();
        loadProducts();
    }
    
    private void updateActiveFilterIndicator() {
        if (currentFilter.isActive()) {
            StringBuilder filterText = new StringBuilder();
            
            if (currentFilter.hasPriceFilter()) {
                filterText.append(currentFilter.getPriceRangeText());
            }
            
            if (currentFilter.hasCategoryFilter()) {
                if (filterText.length() > 0) filterText.append(" • ");
                filterText.append(currentFilter.getCategory());
            }
            
            if (currentFilter.hasBrandFilter()) {
                if (filterText.length() > 0) filterText.append(" • ");
                filterText.append(currentFilter.getBrand());
            }
            
            activeFilterText.setText(filterText.toString());
            activeFilterLayout.setVisibility(View.VISIBLE);
        } else {
            activeFilterLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProductClick(Product product) {
        // TODO: Navigate to product detail activity
        Toast.makeText(this, "Product: " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToCartClick(Product product) {
        // TODO: Add to cart functionality
        Toast.makeText(this, "Added to cart: " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    public void onRetryClick(View view) {
        loadProducts();
    }
} 