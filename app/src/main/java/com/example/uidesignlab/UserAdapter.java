package com.example.uidesignlab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    // Keep the data in a list field
    private List<UserRecord> users = new ArrayList<>();

    // Method to update data and tell the adapter to refresh the screen
    public void setUsers(List<UserRecord> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the row layout from Lab 03 (list_item.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserRecord user = users.get(position);
        // Format the row text as: "id. name" (e.g., "2. John")
        String rowText = user.id + ". " + user.name;
        holder.txtItem.setText(rowText);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // ViewHolder static inner class
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtItem;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find txtItem inside the list_item layout
            txtItem = itemView.findViewById(R.id.txtItem);
        }
    }
}