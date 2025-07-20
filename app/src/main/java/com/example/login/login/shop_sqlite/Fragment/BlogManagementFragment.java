package com.example.login.login.shop_sqlite.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class BlogManagementFragment extends Fragment implements BlogListAdapter.OnBlogClickListener {
    private RecyclerView recyclerView;
    private BlogListAdapter adapter;
    private List<Blog> blogList = new ArrayList<>();
    private FloatingActionButton fabAddBlog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_blog_management, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewBlogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fabAddBlog = view.findViewById(R.id.fabAddBlog);
        fabAddBlog.setOnClickListener(v -> showBlogDialog(null));
        fetchBlogs();
        return view;
    }

    private void fetchBlogs() {
        BlogApiService api = ApiClient.getClient().create(BlogApiService.class);
        api.getAllBlogs().enqueue(new Callback<List<Blog>>() {
            @Override
            public void onResponse(Call<List<Blog>> call, Response<List<Blog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    blogList = response.body();
                    adapter = new BlogListAdapter(blogList, BlogManagementFragment.this, true);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "Không tải được dữ liệu blog", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Blog>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void showBlogDialog(Blog blog) {
        boolean isEdit = blog != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isEdit ? "Edit Blog" : "Add Blog");
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_blog, null);
        EditText etTitle = view.findViewById(R.id.etBlogTitle);
        EditText etContent = view.findViewById(R.id.etBlogContent);
        EditText etCategory = view.findViewById(R.id.etBlogCategory);
        if (isEdit) {
            etTitle.setText(blog.getBlogTittle());
            etContent.setText(blog.getBlogContent());
            etCategory.setText(blog.getBlogCategoryId() != null ? String.valueOf(blog.getBlogCategoryId()) : "");
        }
        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", null);
        final AlertDialog dialog = builder.create();
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
                                Toast.makeText(requireContext(), "Blog updated", Toast.LENGTH_SHORT).show();
                                fetchBlogs();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
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
                                Toast.makeText(requireContext(), "Blog added", Toast.LENGTH_SHORT).show();
                                fetchBlogs();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(requireContext(), "Add failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            });
        });
        dialog.show();
    }

    @Override
    public void onBlogClick(Blog blog) {
        Intent intent = new Intent(requireContext(),
                com.example.login.login.shop_sqlite.Activity.BlogDetailActivity.class);
        intent.putExtra("blogId", blog.getBlogId());
        startActivity(intent);
    }

    // Thêm callback cho edit và delete
    public void onEditBlog(Blog blog) {
        showBlogDialog(blog);
    }

    public void onDeleteBlog(Blog blog) {
        BlogApiService api = ApiClient.getClient().create(BlogApiService.class);
        api.deleteBlog(blog.getBlogId(), true).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Blog deleted", Toast.LENGTH_SHORT).show();
                    fetchBlogs();
                } else {
                    Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}