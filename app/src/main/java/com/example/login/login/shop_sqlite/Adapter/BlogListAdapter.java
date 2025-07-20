package com.example.login.login.shop_sqlite.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.login.shop_sqlite.Models.Blog;
import com.example.login.login.shop_sqlite.R;
import java.util.List;

public class BlogListAdapter extends RecyclerView.Adapter<BlogListAdapter.BlogViewHolder> {
    public interface OnBlogClickListener {
        void onBlogClick(Blog blog);

        void onEditBlog(Blog blog);

        void onDeleteBlog(Blog blog);
    }

    private List<Blog> blogList;
    private OnBlogClickListener listener;
    private boolean showActions = false;

    public BlogListAdapter(List<Blog> blogList, OnBlogClickListener listener) {
        this(blogList, listener, true);
    }

    public BlogListAdapter(List<Blog> blogList, OnBlogClickListener listener, boolean showActions) {
        this.blogList = blogList;
        this.listener = listener;
        this.showActions = showActions;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        holder.tvTitle.setText(blog.getBlogTittle());
        holder.tvContent.setText(blog.getBlogContent());
        holder.itemView.setOnClickListener(v -> listener.onBlogClick(blog));
        if (showActions) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onEditBlog(blog));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteBlog(blog));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent;
        ImageButton btnEdit, btnDelete;

        BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemBlogTitle);
            tvContent = itemView.findViewById(R.id.tvItemBlogSummary);
            btnEdit = itemView.findViewById(R.id.btnEditBlog);
            btnDelete = itemView.findViewById(R.id.btnDeleteBlog);
        }
    }
}