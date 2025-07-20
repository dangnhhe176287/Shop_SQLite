package com.example.login.login.shop_sqlite.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.login.shop_sqlite.Models.Review;
import com.example.login.login.shop_sqlite.R;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;
    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.tvUser.setText("User ID: " + review.getUserId());
        holder.tvContent.setText(review.getContent());
        holder.tvDate.setText(review.getCreatedAt());
    }
    @Override
    public int getItemCount() {
        return reviewList.size();
    }
    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvContent, tvDate;
        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvReviewUser);
            tvContent = itemView.findViewById(R.id.tvReviewContent);
            tvDate = itemView.findViewById(R.id.tvReviewDate);
        }
    }
} 