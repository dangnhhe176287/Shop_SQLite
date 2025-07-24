package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.login.login.shop_sqlite.R;
import android.widget.RatingBar;
import android.widget.EditText;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.login.shop_sqlite.Models.Review;
import com.example.login.login.shop_sqlite.Models.Rating;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.LinkedHashSet;
import java.util.List;
import com.example.login.login.shop_sqlite.Api.RatingApiService;
import com.example.login.login.shop_sqlite.Api.ReviewApiService;
import com.example.login.login.shop_sqlite.Adapter.ReviewAdapter;
import android.util.Log;
import android.widget.Toast;
import com.example.login.login.shop_sqlite.Models.RatingRequest;
import com.example.login.login.shop_sqlite.Models.ReviewRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import com.example.login.login.shop_sqlite.Models.ProductVariantDto;
import com.example.login.login.shop_sqlite.Models.Product;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import android.view.View;
import com.example.login.login.shop_sqlite.Models.CartItemDto;

public class ProductDetailActivity extends AppCompatActivity {
    private RatingBar ratingBarAverage, ratingBarInput;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private EditText etReviewContent;
    private Button btnSubmitReview;
    private int productId;
    private int userId; // Không hardcode nữa
    private Product product;
    private TextView productPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView productImage = findViewById(R.id.productImage);
        TextView productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        TextView productDescription = findViewById(R.id.productDescription);
        TextView productBrand = findViewById(R.id.productBrand);
        TextView productCategory = findViewById(R.id.productCategory);

        ratingBarAverage = findViewById(R.id.ratingBarAverage);
        ratingBarInput = findViewById(R.id.ratingBarInput);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        etReviewContent = findViewById(R.id.etReviewContent);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);

        // Lấy userId từ SharedPreferences
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("current_user_id", -1);

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

        productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            fetchAverageRating(productId);
            fetchReviews(productId);
            fetchProductDetail(productId);
        }
        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    // Helper method to format price
    public String formatPrice(double price) {
        return String.format("$%.2f", price);
    }

    private void fetchAverageRating(int productId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5287/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RatingApiService api = retrofit.create(RatingApiService.class);
        api.getRatingsByProduct(productId).enqueue(new Callback<List<Rating>>() {
            @Override
            public void onResponse(Call<List<Rating>> call, Response<List<Rating>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Rating> ratings = response.body();
                    float avg = 0;
                    for (Rating r : ratings) avg += r.getScore();
                    if (!ratings.isEmpty()) avg /= ratings.size();
                    ratingBarAverage.setRating(avg);
                }
            }
            @Override
            public void onFailure(Call<List<Rating>> call, Throwable t) { }
        });
    }

    private void fetchReviews(int productId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5287/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReviewApiService api = retrofit.create(ReviewApiService.class);
        api.getReviewsByProduct(productId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewAdapter = new ReviewAdapter(response.body());
                    recyclerViewReviews.setAdapter(reviewAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) { }
        });
    }

    private void fetchProductDetail(int productId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getProductById(productId).enqueue(new retrofit2.Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, retrofit2.Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body();
                    setupVariantUI();
                }
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                // Xử lý lỗi
            }
        });
    }

    private void setupVariantUI() {
        LinearLayout variantLayout = findViewById(R.id.variantLayout);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        // Sử dụng LinkedHashMap để giữ thứ tự thuộc tính
        Map<String, Spinner> attributeSpinners = new LinkedHashMap<>();
        Map<String, ArrayAdapter<String>> attributeAdapters = new LinkedHashMap<>();
        List<String> attributeOrder = new ArrayList<>();
        List<Map<String, Object>> allVariants = new LinkedList<>();
        if (product != null && product.getAvailableAttributes() != null) {
            Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
            Map<String, List<String>> availableAttributes = new Gson().fromJson(product.getAvailableAttributes(), type);
            attributeOrder.addAll(availableAttributes.keySet());
            // Gộp tất cả variant value lại thành 1 list
            if (product.getVariants() != null) {
                for (com.example.login.login.shop_sqlite.Models.ProductVariantDto variant : product.getVariants()) {
                    allVariants.addAll(variant.variants);
                }
            }
            // Tạo Spinner và Adapter cho từng thuộc tính
            for (String attr : attributeOrder) {
                // Label
                TextView attrLabel = new TextView(this);
                attrLabel.setText(attr);
                attrLabel.setTextSize(16);
                attrLabel.setPadding(0, 8, 0, 4);
                variantLayout.addView(attrLabel);
                // Spinner
                Spinner spinner = new Spinner(this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(availableAttributes.get(attr)));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                variantLayout.addView(spinner);
                attributeSpinners.put(attr, spinner);
                attributeAdapters.put(attr, adapter);
            }
            // Đăng ký sự kiện chọn cho từng Spinner
            for (int i = 0; i < attributeOrder.size(); i++) {
                final int index = i;
                String attr = attributeOrder.get(i);
                Spinner spinner = attributeSpinners.get(attr);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Lấy lựa chọn trước đó
                        Map<String, String> selected = new HashMap<>();
                        for (int j = 0; j <= index; j++) {
                            String prevAttr = attributeOrder.get(j);
                            selected.put(prevAttr, attributeSpinners.get(prevAttr).getSelectedItem().toString());
                        }
                        // Cập nhật các Spinner phía sau
                        for (int j = index + 1; j < attributeOrder.size(); j++) {
                            String nextAttr = attributeOrder.get(j);
                            List<String> validValues = getValidValuesForAttribute(allVariants, selected, nextAttr);
                            ArrayAdapter<String> adapter = attributeAdapters.get(nextAttr);
                            adapter.clear();
                            adapter.addAll(validValues);
                            adapter.notifyDataSetChanged();
                            // Reset selection về vị trí đầu tiên
                            attributeSpinners.get(nextAttr).setSelection(0);
                        }
                        // Cập nhật giá variant nếu đã chọn đủ
                        boolean allSelected = true;
                        Map<String, String> allSelectedMap = new HashMap<>();
                        for (String key : attributeOrder) {
                            Spinner sp = attributeSpinners.get(key);
                            if (sp.getSelectedItem() == null) {
                                allSelected = false;
                                break;
                            }
                            allSelectedMap.put(key, sp.getSelectedItem().toString());
                        }
                        Log.d("VariantDebug", "Selected: " + allSelectedMap);
                        if (allSelected) {
                            boolean found = false;
                            for (Map<String, Object> vMap : allVariants) {
                                Log.d("VariantDebug", "Variant: " + vMap);
                                boolean match = true;
                                for (String attr : allSelectedMap.keySet()) {
                                    if (!allSelectedMap.get(attr).equals(String.valueOf(vMap.get(attr)))) {
                                        match = false;
                                        break;
                                    }
                                }
                                if (match) {
                                    found = true;
                                    Object priceObj = vMap.get("price");
                                    Log.d("VariantDebug", "Matched price: " + priceObj);
                                    double price = (priceObj != null) ? Double.parseDouble(priceObj.toString()) : 0;
                                    Log.d("VariantDebug", "Set productPrice: " + formatPrice(price));
                                    productPrice.setText(formatPrice(price));
                                    break;
                                }
                            }
                            if (!found) {
                                Log.d("VariantDebug", "No variant matched, show basePrice");
                                productPrice.setText(formatPrice(product.getBasePrice()));
                            }
                        } else {
                            Log.d("VariantDebug", "Not all attributes selected, show basePrice");
                            productPrice.setText(formatPrice(product.getBasePrice()));
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        }
        btnAddToCart.setOnClickListener(v -> {
            // Lấy các thuộc tính đã chọn
            Map<String, String> selected = new HashMap<>();
            for (String attr : attributeSpinners.keySet()) {
                Spinner spinner = attributeSpinners.get(attr);
                selected.put(attr, spinner.getSelectedItem().toString());
            }
            // Tìm variant phù hợp
            Map<String, Object> matchedVariant = null;
            int variantId = 0;
            if (product.getVariants() != null) {
                for (ProductVariantDto variant : product.getVariants()) {
                    for (Map<String, Object> vMap : variant.variants) {
                        boolean match = true;
                        for (String attr : selected.keySet()) {
                            if (!selected.get(attr).equals(String.valueOf(vMap.get(attr)))) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            matchedVariant = vMap;
                            variantId = variant.variantId; // Lấy variantId từ cha
                            break;
                        }
                    }
                    if (matchedVariant != null) break;
                }
            }
            if (matchedVariant == null) {
                Toast.makeText(this, "Vui lòng chọn đủ thuộc tính hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Lấy giá và các thông tin cần thiết
            double price = matchedVariant.containsKey("price") ? Double.parseDouble(matchedVariant.get("price").toString()) : 0;
            double stock = matchedVariant.containsKey("stock") ? Double.parseDouble(matchedVariant.get("stock").toString()) : 0;
            int quantity = 1; // hoặc cho người dùng chọn số lượng

            // Gọi API thêm vào giỏ hàng
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            CartItemDto cartItem = new CartItemDto(product.getProductId(), quantity);
            cartItem.setVariantId(variantId);
            cartItem.setPrice(price);
            apiService.addToCart(userId, cartItem).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Lỗi khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Hàm lọc giá trị hợp lệ cho thuộc tính tiếp theo
    private List<String> getValidValuesForAttribute(List<Map<String, Object>> allVariants, Map<String, String> selected, String nextAttr) {
        Set<String> validValues = new LinkedHashSet<>();
        for (Map<String, Object> v : allVariants) {
            boolean match = true;
            for (String key : selected.keySet()) {
                if (!selected.get(key).equals(String.valueOf(v.get(key)))) {
                    match = false;
                    break;
                }
            }
            if (match && v.containsKey(nextAttr)) {
                validValues.add(String.valueOf(v.get(nextAttr)));
            }
        }
        return new ArrayList<>(validValues);
    }

    private void submitReview() {
        String content = etReviewContent.getText().toString().trim();
        int score = (int) ratingBarInput.getRating();
        if (content.isEmpty() || score == 0) {
            etReviewContent.setError("Vui lòng nhập nhận xét và chọn số sao!");
            return;
        }
        // Gửi review
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5287/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReviewApiService reviewApi = retrofit.create(ReviewApiService.class);
        RatingApiService ratingApi = retrofit.create(RatingApiService.class);
        ReviewRequest review = new ReviewRequest();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setContent(content);
        Log.d("ReviewAPI", "Gửi review: productId=" + review.getProductId() + ", userId=" + review.getUserId() + ", content=" + review.getContent());
        reviewApi.addReview(review).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("ReviewAPI", "onResponse: code=" + response.code() + ", message=" + response.message());
                if (response.isSuccessful()) {
                    fetchReviews(productId);
                    etReviewContent.setText("");
                    Toast.makeText(ProductDetailActivity.this, "Gửi nhận xét thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không thể gửi nhận xét: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("ReviewAPI", "Không thể gửi nhận xét: " + response.code() + " - " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ReviewAPI", "Lỗi gửi nhận xét: " + t.getMessage(), t);
                Toast.makeText(ProductDetailActivity.this, "Lỗi gửi nhận xét: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Gửi rating
        RatingRequest rating = new RatingRequest();
        rating.setProductId(productId);
        rating.setUserId(userId);
        rating.setScore(score);
        Log.d("RatingAPI", "Gửi rating: productId=" + rating.getProductId() + ", userId=" + rating.getUserId() + ", score=" + rating.getScore());
        ratingApi.addRating(rating).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("RatingAPI", "onResponse: code=" + response.code() + ", message=" + response.message());
                if (response.isSuccessful()) {
                    fetchAverageRating(productId);
                    ratingBarInput.setRating(0);
                    Toast.makeText(ProductDetailActivity.this, "Gửi rating thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không thể gửi rating: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("RatingAPI", "Không thể gửi rating: " + response.code() + " - " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("RatingAPI", "Lỗi gửi rating: " + t.getMessage(), t);
                Toast.makeText(ProductDetailActivity.this, "Lỗi gửi rating: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 