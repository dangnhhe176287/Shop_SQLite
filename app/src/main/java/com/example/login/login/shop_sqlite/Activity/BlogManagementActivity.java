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
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import com.example.login.login.shop_sqlite.Utils.FileUtils;

public class BlogManagementActivity extends AppCompatActivity implements BlogListAdapter.OnBlogClickListener {
    private RecyclerView recyclerView;
    private BlogListAdapter adapter;
    private List<Blog> blogList = new ArrayList<>();
    private FloatingActionButton fabAddBlog;
    private static final int PICK_IMAGE_REQUEST = 1001;
    private EditText etThumbnailUrl;
    private Uri selectedImageUri;

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
        EditText etSummary = view.findViewById(R.id.etBlogSummary);
        etThumbnailUrl = view.findViewById(R.id.etBlogThumbnailUrl);
        Button btnPickImage = view.findViewById(R.id.btnPickImage);
        btnPickImage.setOnClickListener(v -> openFileChooser());
        EditText etTags = view.findViewById(R.id.etBlogTags);
        EditText etStatus = view.findViewById(R.id.etBlogStatus);
        if (isEdit) {
            etTitle.setText(blog.getBlogTittle());
            etCategory.setText(blog.getBlogCategoryId() != null ? String.valueOf(blog.getBlogCategoryId()) : "");
            etContent.setText(blog.getBlogContent());
            etSummary.setText(blog.getSummary());
            etThumbnailUrl.setText(blog.getThumbnailUrl());
            etTags.setText(blog.getTags());
            etStatus.setText(blog.getStatus());
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
                String summary = etSummary.getText().toString().trim();
                String thumbnailUrl = etThumbnailUrl.getText().toString().trim();
                String tags = etTags.getText().toString().trim();
                String status = etStatus.getText().toString().trim();
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
                if (selectedImageUri != null) {
                    uploadThumbnailAndAddOrUpdateBlog(isEdit, blog, new Blog(), title, content, categoryId, summary,
                            tags, status, dialog, api);
                } else {
                    // Không chọn ảnh, dùng url nhập tay hoặc rỗng
                    if (isEdit) {
                        blog.setBlogTittle(title);
                        blog.setBlogContent(content);
                        blog.setBlogCategoryId(categoryId);
                        blog.setSummary(summary);
                        blog.setThumbnailUrl(etThumbnailUrl.getText().toString().trim());
                        blog.setTags(tags);
                        blog.setStatus(status);
                        api.updateBlog(blog.getBlogId(), blog).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(BlogManagementActivity.this, "Blog updated", Toast.LENGTH_SHORT)
                                            .show();
                                    fetchBlogs();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(BlogManagementActivity.this, "Update failed", Toast.LENGTH_SHORT)
                                            .show();
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
                        newBlog.setSummary(summary);
                        newBlog.setThumbnailUrl(etThumbnailUrl.getText().toString().trim());
                        newBlog.setTags(tags);
                        newBlog.setStatus(status);
                        api.addBlog(newBlog).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(BlogManagementActivity.this, "Blog added", Toast.LENGTH_SHORT)
                                            .show();
                                    fetchBlogs();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(BlogManagementActivity.this, "Add failed", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(BlogManagementActivity.this, "Network error: " + t.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        });
        dialog.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null
                && data.getData() != null) {
            selectedImageUri = data.getData();
            etThumbnailUrl.setText(selectedImageUri.toString());
        }
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

    private void uploadThumbnailAndAddOrUpdateBlog(boolean isEdit, Blog blog, Blog newBlog, String title,
            String content, Integer categoryId, String summary, String tags, String status, AlertDialog dialog,
            BlogApiService api) {
        try {
            String filePath = FileUtils.getPath(this, selectedImageUri);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody
                    .create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            api.uploadThumbnail(body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseStr = response.body().string();
                            String url = parseUrlFromResponse(responseStr);
                            if (isEdit) {
                                blog.setBlogTittle(title);
                                blog.setBlogContent(content);
                                blog.setBlogCategoryId(categoryId);
                                blog.setSummary(summary);
                                blog.setThumbnailUrl(url);
                                blog.setTags(tags);
                                blog.setStatus(status);
                                api.updateBlog(blog.getBlogId(), blog).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(BlogManagementActivity.this, "Blog updated",
                                                    Toast.LENGTH_SHORT).show();
                                            fetchBlogs();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(BlogManagementActivity.this, "Update failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(BlogManagementActivity.this, "Network error: " + t.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                newBlog.setBlogTittle(title);
                                newBlog.setBlogContent(content);
                                newBlog.setBlogCategoryId(categoryId);
                                newBlog.setSummary(summary);
                                newBlog.setThumbnailUrl(url);
                                newBlog.setTags(tags);
                                newBlog.setStatus(status);
                                api.addBlog(newBlog).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(BlogManagementActivity.this, "Blog added",
                                                    Toast.LENGTH_SHORT).show();
                                            fetchBlogs();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(BlogManagementActivity.this, "Add failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(BlogManagementActivity.this, "Network error: " + t.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            Toast.makeText(BlogManagementActivity.this, "Parse response error", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(BlogManagementActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(BlogManagementActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String parseUrlFromResponse(String responseStr) {
        // Giả sử response dạng {"url":"/uploads/thumbnails/xxx.jpg"}
        int start = responseStr.indexOf(":");
        int end = responseStr.indexOf("}");
        if (start != -1 && end != -1) {
            return responseStr.substring(start + 2, end - 1);
        }
        return "";
    }
}