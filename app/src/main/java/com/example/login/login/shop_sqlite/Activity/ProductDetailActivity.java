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
import java.util.List;
import com.example.login.login.shop_sqlite.Api.RatingApiService;
import com.example.login.login.shop_sqlite.Api.ReviewApiService;
import com.example.login.login.shop_sqlite.Adapter.ReviewAdapter;
import android.util.Log;
import android.widget.Toast;
import com.example.login.login.shop_sqlite.Models.RatingRequest;
import com.example.login.login.shop_sqlite.Models.ReviewRequest;

public class ProductDetailActivity extends AppCompatActivity {
    private RatingBar ratingBarAverage, ratingBarInput;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private EditText etReviewContent;
    private Button btnSubmitReview;
    private int productId;
    private int userId; // Không hardcode nữa

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
        }
        btnSubmitReview.setOnClickListener(v -> submitReview());
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