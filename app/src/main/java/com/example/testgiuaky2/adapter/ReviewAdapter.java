package com.example.testgiuaky2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testgiuaky2.R;
import com.example.testgiuaky2.model.Review;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private List<Review> reviewList;
    private final String currentUserId;
    private final OnReviewActionListener onReviewActionListener;

    // Interface for handling review actions
    public interface OnReviewActionListener {
        void onEditReview(Review review);
        void onDeleteReview(Review review);
    }

    public ReviewAdapter(Context context, String currentUserId, OnReviewActionListener listener) {
        this.context = context;
        this.reviewList = new ArrayList<>();
        this.currentUserId = currentUserId;
        this.onReviewActionListener = listener;
    }

    public void setReviews(List<Review> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        // Set user name
        holder.tvUserName.setText(review.getUser().getFullName());

        // Load user profile image
        if (review.getUser().getPicture() != null && !review.getUser().getPicture().isEmpty()) {
            Glide.with(context)
                    .load(review.getUser().getPicture())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .circleCrop()
                    .into(holder.imgUserAvatar);
        } else {
            holder.imgUserAvatar.setImageResource(R.drawable.placeholder_image);
        }

        // Set rating
        holder.ratingBar.setRating(review.getRating());

        // Set review content
        holder.tvReviewContent.setText(review.getContent());

        // Format and set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(review.getCreatedAt());
        holder.tvReviewDate.setText(formattedDate);

        // Show edit/delete options only for the review owner
        if (currentUserId != null && currentUserId.equals(review.getUser().getId())) {
            holder.btnEditReview.setVisibility(View.VISIBLE);
            holder.btnDeleteReview.setVisibility(View.VISIBLE);

            holder.btnEditReview.setOnClickListener(v ->
                    onReviewActionListener.onEditReview(review));

            holder.btnDeleteReview.setOnClickListener(v ->
                    onReviewActionListener.onDeleteReview(review));
        } else {
            holder.btnEditReview.setVisibility(View.GONE);
            holder.btnDeleteReview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserAvatar;
        TextView tvUserName, tvReviewContent, tvReviewDate;
        RatingBar ratingBar;
        ImageButton btnEditReview, btnDeleteReview;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvReviewContent = itemView.findViewById(R.id.tvReviewContent);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnEditReview = itemView.findViewById(R.id.btnEditReview);
            btnDeleteReview = itemView.findViewById(R.id.btnDeleteReview);
        }
    }
}