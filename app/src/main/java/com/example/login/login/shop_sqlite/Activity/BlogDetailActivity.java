package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.Api.BlogApiService;
import com.example.login.login.shop_sqlite.Models.Blog;
import com.example.login.login.shop_sqlite.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class BlogDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvContent;
    private TextView tvSummary, tvTags, tvStatus, tvViewCount;
    private ImageView ivThumbnail;
    private Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);
        tvTitle = findViewById(R.id.tvBlogTitle);
        tvContent = findViewById(R.id.tvBlogContent);
        tvSummary = findViewById(R.id.tvBlogSummary);
        tvTags = findViewById(R.id.tvBlogTags);
        tvStatus = findViewById(R.id.tvBlogStatus);
        tvViewCount = findViewById(R.id.tvBlogViewCount);
        ivThumbnail = findViewById(R.id.ivBlogThumbnail);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        int blogId = getIntent().getIntExtra("blogId", -1);
        if (blogId != -1) {
            increaseViewAndFetchDetail(blogId);
        } else {
            Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void increaseViewAndFetchDetail(int blogId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5287/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BlogApiService api = retrofit.create(BlogApiService.class);
        api.increaseView(blogId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Sau khi tăng view, lấy lại chi tiết blog
                fetchBlogDetail(blogId);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Dù lỗi vẫn lấy chi tiết blog
                fetchBlogDetail(blogId);
            }
        });
    }

    private void fetchBlogDetail(int blogId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5287/") // Đổi lại nếu backend khác
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BlogApiService api = retrofit.create(BlogApiService.class);
        api.getBlogById(blogId).enqueue(new Callback<Blog>() {
            @Override
            public void onResponse(Call<Blog> call, Response<Blog> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Blog blog = response.body();
                    tvTitle.setText(blog.getBlogTittle());
                    tvContent.setText(blog.getBlogContent());
                    tvSummary.setText(blog.getSummary());
                    tvTags.setText(blog.getTags());
                    tvStatus.setText(blog.getStatus());
                    tvViewCount.setText("Views: " + blog.getViewCount());
                    if (blog.getThumbnailUrl() != null && !blog.getThumbnailUrl().isEmpty()) {
                        Glide.with(BlogDetailActivity.this)
                                .load(blog.getThumbnailUrl())
                                .placeholder(R.drawable.ic_err_image_background)
                                .error(R.drawable.ic_err_image_background)
                                .into(ivThumbnail);
                    } else {
                        ivThumbnail.setImageResource(R.drawable.ic_err_image_background);
                    }
                } else {
                    Toast.makeText(BlogDetailActivity.this, "Không tải được chi tiết blog", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Blog> call, Throwable t) {
                Toast.makeText(BlogDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}