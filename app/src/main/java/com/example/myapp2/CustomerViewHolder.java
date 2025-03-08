package com.example.myapp2;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewName;
    public TextView textViewAddress;

    public CustomerViewHolder(View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.textViewName);
        textViewAddress = itemView.findViewById(R.id.textViewAddress);
    }
}