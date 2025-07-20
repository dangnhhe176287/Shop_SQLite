package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.login.shop_sqlite.Adapter.BlogListAdapter;
import com.example.login.login.shop_sqlite.Api.BlogApiService;
import com.example.login.login.shop_sqlite.Models.Blog;
import com.example.login.login.shop_sqlite.R;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BlogListActivity extends AppCompatActivity implements BlogListAdapter.OnBlogClickListener {
    private RecyclerView recyclerView;
    private BlogListAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);
        recyclerView = findViewById(R.id.recyclerViewBlogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchBlogs();
    }
    private void fetchBlogs() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5287/") // Đổi lại nếu backend khác
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BlogApiService api = retrofit.create(BlogApiService.class);
        api.getAllBlogs().enqueue(new Callback<List<Blog>>() {
            @Override
            public void onResponse(Call<List<Blog>> call, Response<List<Blog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new BlogListAdapter(response.body(), BlogListActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(BlogListActivity.this, "Không tải được dữ liệu blog", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Blog>> call, Throwable t) {
                Toast.makeText(BlogListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBlogClick(Blog blog) {
        Intent intent = new Intent(this, BlogDetailActivity.class);
        intent.putExtra("blogId", blog.getBlogId());
        startActivity(intent);
    }
} 