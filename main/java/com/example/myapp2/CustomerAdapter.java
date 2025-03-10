package com.example.myapp2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private static final String TAG = "CustomerAdapter";
    private Context context;
    private ArrayList<Customer> customers;
    private DatabaseHelper dbHelper;

    public CustomerAdapter(Context context, ArrayList<Customer> customers) {
        this.context = context;
        this.customers = new ArrayList<>(customers);
        this.dbHelper = DatabaseHelper.getInstance(context);
        Log.d(TAG, "Adapter initialized, customers size: " + (this.customers != null ? this.customers.size() : 0));
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.textViewName.setText(customer.getName());
        holder.textViewAddress.setText(customer.getAddress());
        Log.d(TAG, "Binding customer at position " + position + ": " + customer.getName());

        ArrayList<Worklog> worklogs = dbHelper.getWorklogsByCustomer(customer.getId());
        boolean hasActiveWorklog = false;
        for (Worklog worklog : worklogs) {
            if (worklog.getClockOut() == null) {
                hasActiveWorklog = true;
                break;
            }
        }
        holder.buttonClockIn.setEnabled(!hasActiveWorklog);
        holder.buttonClockOut.setEnabled(hasActiveWorklog);
        Log.d(TAG, "Customer " + customer.getName() + ": hasActiveWorklog=" + hasActiveWorklog);

        holder.buttonClockIn.setOnClickListener(v -> {
            Worklog worklog = new Worklog(-1, customer.getId(), "Work started", System.currentTimeMillis(), null);
            long newId = dbHelper.addWorklog(worklog);
            Log.d(TAG, "Clocked in for " + customer.getName() + ", new worklog ID: " + newId);
            holder.buttonClockIn.setEnabled(false);
            holder.buttonClockOut.setEnabled(true);
            Toast.makeText(context, "Clocked in for " + customer.getName(), Toast.LENGTH_SHORT).show();
        });

        holder.buttonClockOut.setOnClickListener(v -> {
            ArrayList<Worklog> freshWorklogs = dbHelper.getWorklogsByCustomer(customer.getId());
            Log.d(TAG, "Clock out attempt for " + customer.getName() + ", worklogs size: " + freshWorklogs.size());
            for (Worklog worklog : freshWorklogs) {
                if (worklog.getClockOut() == null) {
                    Log.d(TAG, "Found active worklog ID: " + worklog.getId());
                    worklog.setClockOut(System.currentTimeMillis());
                    dbHelper.updateWorklog(worklog);
                    holder.buttonClockIn.setEnabled(true);
                    holder.buttonClockOut.setEnabled(false);
                    Toast.makeText(context, "Clocked out for " + customer.getName(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Clocked out for " + customer.getName());
                    break;
                }
            }
            if (freshWorklogs.isEmpty()) {
                Log.d(TAG, "No worklogs found for " + customer.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called, size: " + (customers != null ? customers.size() : 0));
        return customers != null ? customers.size() : 0;
    }

    public void updateData(ArrayList<Customer> newCustomers) {
        Log.d(TAG, "Updating data, old size: " + (customers != null ? customers.size() : 0));
        this.customers = new ArrayList<>();
        if (newCustomers != null) {
            this.customers.addAll(newCustomers);
            Log.d(TAG, "Updated data, new size: " + customers.size());
        } else {
            Log.d(TAG, "Updated with null, size: 0");
        }
        notifyDataSetChanged();
        Log.d(TAG, "notifyDataSetChanged called, final size: " + customers.size());
    }

    public Customer getCustomerAt(int position) {
        return customers.get(position);
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewAddress;
        public Button buttonClockIn;
        public Button buttonClockOut;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            buttonClockIn = itemView.findViewById(R.id.buttonClockIn);
            buttonClockOut = itemView.findViewById(R.id.buttonClockOut);
        }
    }
}

