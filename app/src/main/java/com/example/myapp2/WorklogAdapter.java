package com.example.myapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WorklogAdapter extends RecyclerView.Adapter<WorklogAdapter.WorklogViewHolder> {
    private Context context;
    private ArrayList<Worklog> worklogs;

    public WorklogAdapter(Context context, ArrayList<Worklog> worklogs) {
        this.context = context;
        this.worklogs = worklogs;
    }

    @Override
    public WorklogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new WorklogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorklogViewHolder holder, int position) {
        Worklog worklog = worklogs.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
        String clockIn = sdf.format(new Date(worklog.getClockIn()));
        String clockOut = worklog.getClockOut() != null ? sdf.format(new Date(worklog.getClockOut())) : "Active";
        holder.text1.setText(worklog.getDescription());
        holder.text2.setText("In: " + clockIn + " | Out: " + clockOut);
    }

    @Override
    public int getItemCount() {
        return worklogs != null ? worklogs.size() : 0;
    }

    public static class WorklogViewHolder extends RecyclerView.ViewHolder {
        public TextView text1, text2;

        public WorklogViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}