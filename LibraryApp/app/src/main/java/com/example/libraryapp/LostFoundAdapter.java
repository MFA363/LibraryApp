package com.example.libraryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LostFoundAdapter extends RecyclerView.Adapter<LostFoundAdapter.ViewHolder> {

    private List<LostItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClaimClick(LostItem item);
    }

    public LostFoundAdapter(List<LostItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lost_found, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LostItem item = itemList.get(position);
        holder.desc.setText(item.getDescription());
        holder.date.setText("Found: " + item.getDateFound());
        holder.location.setText("Loc: " + item.getLocationFound());

        if ("Claim Pending".equals(item.getStatus())) {
            holder.btnClaim.setText("PENDING");
            holder.btnClaim.setEnabled(false);
        } else {
            holder.btnClaim.setText("CLAIM");
            holder.btnClaim.setEnabled(true);
            holder.btnClaim.setOnClickListener(v -> listener.onClaimClick(item));
        }
    }

    @Override
    public int getItemCount() { return itemList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView desc, date, location;
        Button btnClaim;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.tvDescription);
            date = itemView.findViewById(R.id.tvDate);
            location = itemView.findViewById(R.id.tvLocation);
            btnClaim = itemView.findViewById(R.id.btnClaim);
        }
    }
}
