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
import com.bumptech.glide.Glide;
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
        
        // Load user info
        String userName = post.getUserName();
        if (userName != null && !userName.isEmpty()) {
            holder.tvUserName.setText(userName);
        } else {
            holder.tvUserName.setText("Người dùng");
        }
        
        // Load user avatar
        String userAvatar = post.getUserAvatar();
        String baseUrl = "http://10.0.2.2:5125";
        try {
            com.example.paw_help.api.RetrofitClient client = com.example.paw_help.api.RetrofitClient.getInstance(context);
            baseUrl = client.getImageBaseUrl();
        } catch (Exception e) {
            // Fallback to default
        }
        
        if (userAvatar != null && !userAvatar.isEmpty()) {
            String fullImageUrl = userAvatar.startsWith("http") 
                ? userAvatar 
                : baseUrl + userAvatar;

            // Show avatar image, hide initials
            holder.imgUserAvatar.setVisibility(View.VISIBLE);
            holder.tvUserAvatarInitials.setVisibility(View.GONE);

            Glide.with(context)
                .load(fullImageUrl)
                .placeholder(R.drawable.avatar_gradient_background)
                .error(R.drawable.avatar_gradient_background)
                .circleCrop()
                .into(holder.imgUserAvatar);
        } else {
            // Show initials, hide avatar image
            holder.imgUserAvatar.setVisibility(View.GONE);
            holder.tvUserAvatarInitials.setVisibility(View.VISIBLE);

            // Generate initials from user name
            String initials = generateInitials(userName);
            holder.tvUserAvatarInitials.setText(initials);
        }
        
        // Load ảnh với Glide - ưu tiên imageUrl, fallback về imageResId
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            // Reuse baseUrl đã được khai báo ở trên
            try {
                com.example.paw_help.api.RetrofitClient client = com.example.paw_help.api.RetrofitClient.getInstance(context);
                baseUrl = client.getImageBaseUrl();
            } catch (Exception e) {
                // Fallback to default - baseUrl đã được set ở trên
            }
            
            String fullImageUrl = post.getImageUrl().startsWith("http") 
                ? post.getImageUrl() 
                : baseUrl + post.getImageUrl();
            
            Glide.with(context)
                .load(fullImageUrl)
                .placeholder(post.getImageResId())
                .error(post.getImageResId())
                .centerCrop()
                .into(holder.imgRescuePhoto);
        } else {
            holder.imgRescuePhoto.setImageResource(post.getImageResId());
        }

        // Set status badge color based on status
        int statusColor;
        int bgColor;
        String status = post.getStatus();
        if (status != null) {
            if (status.equals("Đã cứu")) {
                statusColor = 0xFF4CAF50;
                bgColor = 0xFFE8F5E9;
            } else if (status.equals("Đang xử lý") || status.equals("Đang cứu")) {
                statusColor = 0xFFFF9800;
                bgColor = 0xFFFFF3E0;
            } else if (status.equals("Chờ xử lý") || status.equals("Chờ cứu")) {
                statusColor = 0xFFE91E63;
                bgColor = 0xFFFADADD;
            } else {
                statusColor = 0xFF757575;
                bgColor = 0xFFE0E0E0;
            }
        } else {
            statusColor = 0xFF757575;
            bgColor = 0xFFE0E0E0;
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

    private String generateInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "U";
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0) {
            return "U";
        }

        if (parts.length == 1) {
            String first = parts[0];
            if (first.length() >= 2) {
                return first.substring(0, 2).toUpperCase();
            }
            return first.substring(0, 1).toUpperCase();
        }

        String firstInitial = parts[0].substring(0, 1).toUpperCase();
        String lastInitial = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return firstInitial + lastInitial;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardRescueItem, btnViewDetails;
        ImageView imgRescuePhoto, imgUserAvatar;
        TextView tvDescription, tvLocation, tvAnimalType, tvStatus, tvTimestamp, tvUserName, tvUserAvatarInitials;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRescueItem = itemView.findViewById(R.id.cardRescueItem);
            imgRescuePhoto = itemView.findViewById(R.id.imgRescuePhoto);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAnimalType = itemView.findViewById(R.id.tvAnimalType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserAvatarInitials = itemView.findViewById(R.id.tvUserAvatarInitials);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}

