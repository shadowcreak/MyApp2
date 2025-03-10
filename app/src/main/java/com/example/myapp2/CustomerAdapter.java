package com.example.myapp2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private List<Customer> customerList;
    private DatabaseHelper databaseHelper;

    public CustomerAdapter(List<Customer> customerList, DatabaseHelper databaseHelper) {
        this.customerList = customerList;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.customerNameTextView.setText(customer.getName());

        // Check mowing status
        List<Worklog> worklogs = databaseHelper.getWorklogsByCustomer(customer.getId());
        boolean mowedThisWeek = isMowedThisWeek(worklogs);
        holder.mowingStatusImageView.setVisibility(mowedThisWeek ? View.VISIBLE : View.GONE);

        // Check if there's an open worklog (clockIn but no clockOut)
        Worklog openWorklog = getOpenWorklog(worklogs);
        if (openWorklog == null) {
            holder.clockInButton.setEnabled(true);
            holder.clockOutButton.setEnabled(false);
        } else {
            holder.clockInButton.setEnabled(false);
            holder.clockOutButton.setEnabled(true);
        }

        // Clock In button logic
        holder.clockInButton.setOnClickListener(v -> {
            Worklog newWorklog = new Worklog(
                    0, // ID will be set by database
                    customer.getId(),
                    System.currentTimeMillis(), // clockIn
                    null, // clockOut (null until clocked out)
                    "Mowing" // description
            );
            databaseHelper.addWorklog(newWorklog);
            notifyItemChanged(position);
        });

        // Clock Out button logic
        holder.clockOutButton.setOnClickListener(v -> {
            if (openWorklog != null) {
                openWorklog.setClockOut(System.currentTimeMillis());
                databaseHelper.updateWorklog(openWorklog);
                notifyItemChanged(position);
            }
        });

        // Make the entire row clickable to open CustomerDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CustomerDetailsActivity.class);
            intent.putExtra("customerId", customer.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    private boolean isMowedThisWeek(List<Worklog> worklogs) {
        if (worklogs == null || worklogs.isEmpty()) return false;

        Collections.sort(worklogs, new Comparator<Worklog>() {
            @Override
            public int compare(Worklog w1, Worklog w2) {
                return Long.compare(w2.getClockIn(), w1.getClockIn());
            }
        });

        long latestClockIn = worklogs.get(0).getClockIn();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfWeek = cal.getTimeInMillis();

        return latestClockIn >= startOfWeek;
    }

    private Worklog getOpenWorklog(List<Worklog> worklogs) {
        if (worklogs == null || worklogs.isEmpty()) return null;
        for (Worklog worklog : worklogs) {
            if (worklog.getClockOut() == null) {
                return worklog;
            }
        }
        return null;
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        public TextView customerNameTextView;
        public Button clockInButton, clockOutButton;
        public ImageView mowingStatusImageView;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            clockInButton = itemView.findViewById(R.id.clockInButton);
            clockOutButton = itemView.findViewById(R.id.clockOutButton);
            mowingStatusImageView = itemView.findViewById(R.id.mowingStatusImageView);
        }
    }
}