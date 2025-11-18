package com.example.paw_help;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RescuePostAdapter extends RecyclerView.Adapter<RescuePostAdapter.ViewHolder> {

    private Context context;
    private List<RescuePost> posts;
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(RescuePost post);
    }

    public RescuePostAdapter(Context context, List<RescuePost> posts, OnPostClickListener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rescue_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RescuePost post = posts.get(position);

        holder.tvDescription.setText(post.getDescription());
        holder.tvLocation.setText(post.getLocation());
        holder.tvAnimalType.setText(post.getAnimalType());
        holder.tvStatus.setText(post.getStatus());
        holder.tvTimestamp.setText(post.getTimestamp());
        holder.imgRescuePhoto.setImageResource(post.getImageResId());

        // Set status badge color based on status
        int statusColor;
        int bgColor;
        if (post.getStatus().equals("Đã cứu")) {
            statusColor = 0xFF4CAF50;
            bgColor = 0xFFE8F5E9;
        } else if (post.getStatus().equals("Đang xử lý")) {
            statusColor = 0xFFFF9800;
            bgColor = 0xFFFFF3E0;
        } else {
            statusColor = 0xFFE91E63;
            bgColor = 0xFFFADADD;
        }
        holder.tvStatus.setTextColor(statusColor);
        holder.tvStatus.setBackgroundColor(bgColor);

        // Click listener
        holder.cardRescueItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(post);
            }
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardRescueItem, btnViewDetails;
        ImageView imgRescuePhoto;
        TextView tvDescription, tvLocation, tvAnimalType, tvStatus, tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRescueItem = itemView.findViewById(R.id.cardRescueItem);
            imgRescuePhoto = itemView.findViewById(R.id.imgRescuePhoto);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAnimalType = itemView.findViewById(R.id.tvAnimalType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}

