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

public class BlogDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvContent;
    private Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);
        tvTitle = findViewById(R.id.tvBlogTitle);
        tvContent = findViewById(R.id.tvBlogContent);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        int blogId = getIntent().getIntExtra("blogId", -1);
        if (blogId != -1) {
            fetchBlogDetail(blogId);
        } else {
            Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
            finish();
        }
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