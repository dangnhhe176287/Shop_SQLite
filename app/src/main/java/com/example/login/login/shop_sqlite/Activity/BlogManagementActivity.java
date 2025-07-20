package com.example.login.login.shop_sqlite.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.login.shop_sqlite.Adapter.BlogListAdapter;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.BlogApiService;
import com.example.login.login.shop_sqlite.Models.Blog;
import com.example.login.login.shop_sqlite.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogManagementActivity extends AppCompatActivity implements BlogListAdapter.OnBlogClickListener {
    private RecyclerView recyclerView;
    private BlogListAdapter adapter;
    private List<Blog> blogList = new ArrayList<>();
    private FloatingActionButton fabAddBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_management);
        recyclerView = findViewById(R.id.recyclerViewBlogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fabAddBlog = findViewById(R.id.fabAddBlog);
        fabAddBlog.setOnClickListener(v -> showBlogDialog(null));
        fetchBlogs();
    }

    private void fetchBlogs() {
        BlogApiService api = ApiClient.getClient().create(BlogApiService.class);
        api.getAllBlogs().enqueue(new Callback<List<Blog>>() {
            @Override
            public void onResponse(Call<List<Blog>> call, Response<List<Blog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    blogList = response.body();
                    adapter = new BlogListAdapter(blogList, BlogManagementActivity.this, true);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(BlogManagementActivity.this, "Không tải được dữ liệu blog", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Blog>> call, Throwable t) {
                Toast.makeText(BlogManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void showBlogDialog(Blog blog) {
        boolean isEdit = blog != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Edit Blog" : "Add Blog");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_blog, null);
        EditText etTitle = view.findViewById(R.id.etBlogTitle);
        EditText etCategory = view.findViewById(R.id.etBlogCategory);
        EditText etContent = view.findViewById(R.id.etBlogContent);
        if (isEdit) {
            etTitle.setText(blog.getBlogTittle());
            etCategory.setText(blog.getBlogCategoryId() != null ? String.valueOf(blog.getBlogCategoryId()) : "");
            etContent.setText(blog.getBlogContent());
        }
        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();
                String categoryStr = etCategory.getText().toString().trim();
                Integer categoryId = categoryStr.isEmpty() ? null : Integer.valueOf(categoryStr);
                if (title.isEmpty()) {
                    etTitle.setError("Title required");
                    etTitle.requestFocus();
                    return;
                }
                if (content.isEmpty()) {
                    etContent.setError("Content required");
                    etContent.requestFocus();
                    return;
                }
                BlogApiService api = ApiClient.getClient().create(BlogApiService.class);
                if (isEdit) {
                    blog.setBlogTittle(title);
                    blog.setBlogContent(content);
                    blog.setBlogCategoryId(categoryId);
                    api.updateBlog(blog.getBlogId(), blog).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(BlogManagementActivity.this, "Blog updated", Toast.LENGTH_SHORT).show();
                                fetchBlogs();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(BlogManagementActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(BlogManagementActivity.this, "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Blog newBlog = new Blog();
                    newBlog.setBlogTittle(title);
                    newBlog.setBlogContent(content);
                    newBlog.setBlogCategoryId(categoryId);
                    api.addBlog(newBlog).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(BlogManagementActivity.this, "Blog added", Toast.LENGTH_SHORT).show();
                                fetchBlogs();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(BlogManagementActivity.this, "Add failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(BlogManagementActivity.this, "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
        dialog.show();
    }

    @Override
    public void onBlogClick(Blog blog) {
        Intent intent = new Intent(this, BlogDetailActivity.class);
        intent.putExtra("blogId", blog.getBlogId());
        startActivity(intent);
    }

    // Thêm callback cho edit và delete
    public void onEditBlog(Blog blog) {
        showBlogDialog(blog);
    }

    public void onDeleteBlog(Blog blog) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Blog")
                .setMessage("Are you sure you want to delete this blog?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    BlogApiService api = ApiClient.getClient().create(BlogApiService.class);
                    api.deleteBlog(blog.getBlogId(), true).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(BlogManagementActivity.this, "Blog deleted", Toast.LENGTH_SHORT).show();
                                fetchBlogs();
                            } else {
                                Toast.makeText(BlogManagementActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(BlogManagementActivity.this, "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}