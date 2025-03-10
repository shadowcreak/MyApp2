package com.example.myapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText; // Added import
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorklogAdapter extends RecyclerView.Adapter<WorklogAdapter.WorklogViewHolder> {
    private List<Worklog> worklogList;
    private DatabaseHelper databaseHelper;
    private Context context;

    public WorklogAdapter(Context context, List<Worklog> worklogList, DatabaseHelper databaseHelper) {
        this.context = context;
        this.worklogList = worklogList;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public WorklogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worklog, parent, false);
        return new WorklogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorklogViewHolder holder, int position) {
        Worklog worklog = worklogList.get(position);
        holder.worklogDescriptionTextView.setText(worklog.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        String clockIn = sdf.format(new Date(worklog.getClockIn()));
        String clockOut = worklog.getClockOut() != null ? sdf.format(new Date(worklog.getClockOut())) : "Not clocked out";
        holder.worklogTimesTextView.setText("Clock In: " + clockIn + ", Clock Out: " + clockOut);

        holder.editWorklogButton.setOnClickListener(v -> showEditDialog(worklog, position));
    }

    @Override
    public int getItemCount() {
        return worklogList.size();
    }

    private void showEditDialog(Worklog worklog, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Worklog");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_worklog, null);
        builder.setView(dialogView);

        TextInputEditText editDescription = dialogView.findViewById(R.id.editDescription);
        TextInputEditText editClockIn = dialogView.findViewById(R.id.editClockIn);
        TextInputEditText editClockOut = dialogView.findViewById(R.id.editClockOut);

        editDescription.setText(worklog.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        editClockIn.setText(sdf.format(new Date(worklog.getClockIn())));
        editClockOut.setText(worklog.getClockOut() != null ? sdf.format(new Date(worklog.getClockOut())) : "");

        builder.setPositiveButton("Save", (dialog, which) -> {
            String description = editDescription.getText().toString();
            String clockInStr = editClockIn.getText().toString();
            String clockOutStr = editClockOut.getText().toString();

            try {
                long clockInTime = sdf.parse(clockInStr).getTime();
                long clockOutTime = clockOutStr.isEmpty() ? 0 : sdf.parse(clockOutStr).getTime();
                worklog.setDescription(description);
                worklog.setClockIn(clockInTime);
                worklog.setClockOut(clockOutTime == 0 ? null : clockOutTime);
                databaseHelper.updateWorklog(worklog);
                notifyItemChanged(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static class WorklogViewHolder extends RecyclerView.ViewHolder {
        public TextView worklogDescriptionTextView, worklogTimesTextView;
        public Button editWorklogButton;

        public WorklogViewHolder(View itemView) {
            super(itemView);
            worklogDescriptionTextView = itemView.findViewById(R.id.worklogDescriptionTextView);
            worklogTimesTextView = itemView.findViewById(R.id.worklogTimesTextView);
            editWorklogButton = itemView.findViewById(R.id.editWorklogButton);
        }
    }
}