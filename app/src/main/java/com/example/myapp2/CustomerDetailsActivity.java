package com.example.myapp2;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CustomerDetailsActivity extends AppCompatActivity {
    private static final String TAG = "CustomerDetailsActivity";
    private TextView textViewName, textViewAddress, textViewRate;
    private RecyclerView recyclerViewWorklogs;
    private WorklogAdapter worklogAdapter;
    private DatabaseHelper dbHelper;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        textViewName = findViewById(R.id.textViewName);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewRate = findViewById(R.id.textViewRate);
        recyclerViewWorklogs = findViewById(R.id.recyclerViewWorklogs);
        dbHelper = DatabaseHelper.getInstance(this);

        customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);
        if (customerId != -1) {
            loadCustomerDetails();
        } else {
            Log.e(TAG, "No customer ID provided");
            finish();
        }
    }

    private void loadCustomerDetails() {
        Customer customer = dbHelper.getCustomerById(customerId);
        if (customer != null) {
            textViewName.setText(customer.getName());
            textViewAddress.setText(customer.getAddress());
            textViewRate.setText(String.valueOf(customer.getRate()));
            loadWorklogs();
        } else {
            Log.e(TAG, "Customer not found for ID: " + customerId);
            finish();
        }
    }

    private void loadWorklogs() {
        ArrayList<Worklog> worklogs = dbHelper.getWorklogsByCustomer(customerId);
        worklogAdapter = new WorklogAdapter(this, worklogs);
        recyclerViewWorklogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWorklogs.setAdapter(worklogAdapter);
        Log.d(TAG, "Loaded " + worklogs.size() + " worklogs for customer ID: " + customerId);
    }
}