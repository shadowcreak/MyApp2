package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
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

        Intent intent = getIntent();
        customerId = intent.getIntExtra("CUSTOMER_ID", -1);

        if (customerId != -1) {
            loadCustomerData();
            setupWorklogs();
        } else {
            Toast.makeText(this, "Invalid customer ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadCustomerData() {
        Customer customer = dbHelper.getCustomerById(customerId);
        if (customer != null) {
            textViewName.setText("Name: " + customer.getName());
            textViewAddress.setText("Address: " + customer.getAddress());
            textViewRate.setText("Rate: " + customer.getRate());
            // Add notes if desired: textViewNotes.setText("Notes: " + customer.getNotes());
        } else {
            Toast.makeText(this, "Customer not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupWorklogs() {
        ArrayList<Worklog> worklogs = dbHelper.getWorklogsByCustomer(customerId);
        worklogAdapter = new WorklogAdapter(this, worklogs, dbHelper);
        recyclerViewWorklogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWorklogs.setAdapter(worklogAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (customerId != -1) {
            setupWorklogs(); // Refresh worklogs
        }
    }
}