package com.example.myapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorklogAdapter extends RecyclerView.Adapter<WorklogAdapter.WorklogViewHolder> {
    private Context context;
    private List<Worklog> worklogs;
    private DatabaseHelper dbHelper;

    public WorklogAdapter(Context context, List<Worklog> worklogs, DatabaseHelper dbHelper) {
        this.context = context;
        this.worklogs = worklogs;
        this.dbHelper = dbHelper;
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

        // Add Edit button functionality
        holder.itemView.setOnClickListener(v -> showEditDialog(worklog, position));
    }

    private void showEditDialog(Worklog worklog, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Worklog");

        // Inflate a simple layout for editing
        View dialogView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null);
        TextView editDescription = dialogView.findViewById(android.R.id.text1);
        TextView editClockOut = dialogView.findViewById(android.R.id.text2);

        editDescription.setText(worklog.getDescription());
        editClockOut.setText(worklog.getClockOut() != null ? "Clock Out: " + new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(new Date(worklog.getClockOut())) : "Clock Out: Not Set");

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newDescription = editDescription.getText().toString().trim();
                    // For now, we'll assume clock out editing is via a separate input (add later if needed)
                    worklog.setDescription(newDescription);
                    dbHelper.updateWorklog(worklog);
                    notifyItemChanged(position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
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