package com.example.paw_help;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RescueHistoryAdapter extends RecyclerView.Adapter<RescueHistoryAdapter.ViewHolder> {

    private Context context;
    private List<RescueHistory> historyList;
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onHistoryClick(RescueHistory history);
    }

    public RescueHistoryAdapter(Context context, List<RescueHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    public void setOnHistoryClickListener(OnHistoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rescue_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RescueHistory history = historyList.get(position);

        holder.tvTitle.setText(history.getTitle());
        holder.tvLocation.setText(history.getLocation());
        holder.tvDate.setText(history.getDate());
        holder.imgPhoto.setImageResource(history.getImageResId());

        // Show/hide checkbox based on completion status
        holder.imgCheckbox.setVisibility(history.isCompleted() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHistoryClick(history);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDate;
        ImageView imgPhoto, imgCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvLocation = itemView.findViewById(R.id.tvHistoryLocation);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            imgPhoto = itemView.findViewById(R.id.imgHistoryPhoto);
            imgCheckbox = itemView.findViewById(R.id.imgCheckbox);
        }
    }
}

