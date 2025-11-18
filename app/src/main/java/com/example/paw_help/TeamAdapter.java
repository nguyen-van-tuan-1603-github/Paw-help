package com.example.paw_help;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private Context context;
    private List<TeamMember> teamMembers;

    public TeamAdapter(Context context, List<TeamMember> teamMembers) {
        this.context = context;
        this.teamMembers = teamMembers;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_member, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        TeamMember member = teamMembers.get(position);

        holder.tvName.setText(member.getName());
        holder.tvRole.setText(member.getRole());

        if (member.getPosition() != null && !member.getPosition().isEmpty()) {
            holder.tvPosition.setText(member.getPosition());
            holder.tvPosition.setVisibility(View.VISIBLE);
        } else {
            holder.tvPosition.setVisibility(View.GONE);
        }

        if (member.getDescription() != null && !member.getDescription().isEmpty()) {
            holder.tvDescription.setText(member.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        if (member.getTeam() != null && !member.getTeam().isEmpty()) {
            holder.tvTeam.setText(member.getTeam());
            holder.tvTeam.setVisibility(View.VISIBLE);
        } else {
            holder.tvTeam.setVisibility(View.GONE);
        }

        holder.imgAvatar.setImageResource(member.getImageResId());

        // Email button click
        holder.btnEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + member.getEmail()));
            context.startActivity(Intent.createChooser(emailIntent, "Gửi email"));
        });

        // Phone button click
        holder.btnPhone.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:" + member.getPhone()));
            context.startActivity(phoneIntent);
        });
    }

    @Override
    public int getItemCount() {
        return teamMembers.size();
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, btnEmail, btnPhone;
        TextView tvName, tvRole, tvPosition, tvDescription, tvTeam;
        CardView cardView;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTeam = itemView.findViewById(R.id.tvTeam);
            btnEmail = itemView.findViewById(R.id.btnEmail);
            btnPhone = itemView.findViewById(R.id.btnPhone);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

