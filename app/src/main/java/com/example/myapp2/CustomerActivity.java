package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class CustomerActivity extends AppCompatActivity {
    private static final String TAG = "CustomerActivity";
    private TextInputEditText editName, editAddress, editRate, editNotes;
    private Spinner contractorSpinner;
    private Button saveButton, deleteButton;
    private DatabaseHelper dbHelper;
    private int customerId = -1;
    private ArrayList<Contractor> contractors;
    private RecyclerView worklogsRecyclerView;
    private WorklogAdapter worklogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer); // Reuse edit_customer layout

        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editRate = findViewById(R.id.editRate);
        editNotes = findViewById(R.id.editNotes);
        contractorSpinner = findViewById(R.id.contractorSpinner);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.buttonDelete); // Assuming buttonDelete exists
        worklogsRecyclerView = findViewById(R.id.worklogsRecyclerView);
        dbHelper = DatabaseHelper.getInstance(this);

        Intent intent = getIntent();
        customerId = intent.getIntExtra("CUSTOMER_ID", -1);

        loadContractors();
        if (customerId != -1) {
            loadCustomerData();
            setupWorklogs();
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveCustomer());
        deleteButton.setOnClickListener(v -> confirmDelete());
    }

    private void loadContractors() {
        contractors = dbHelper.getAllContractors();
        ArrayList<String> contractorNames = new ArrayList<>();
        contractorNames.add("Direct to Customer");
        for (Contractor contractor : contractors) {
            contractorNames.add(contractor.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contractorNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractorSpinner.setAdapter(adapter);
    }

    private void loadCustomerData() {
        Customer customer = dbHelper.getCustomerById(customerId);
        if (customer != null) {
            editName.setText(customer.getName());
            editAddress.setText(customer.getAddress());
            editRate.setText(String.valueOf(customer.getRate()));
            editNotes.setText(customer.getNotes() != null ? customer.getNotes() : "");
            int contractorId = customer.getContractorId();
            if (contractorId == -1) {
                contractorSpinner.setSelection(0);
            } else {
                for (int i = 0; i < contractors.size(); i++) {
                    if (contractors.get(i).getId() == contractorId) {
                        contractorSpinner.setSelection(i + 1);
                        break;
                    }
                }
            }
        }
    }

    private void setupWorklogs() {
        ArrayList<Worklog> worklogs = dbHelper.getWorklogsByCustomer(customerId);
        worklogAdapter = new WorklogAdapter(this, worklogs, dbHelper);
        worklogsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        worklogsRecyclerView.setAdapter(worklogAdapter);
    }

    private void saveCustomer() {
        String name = editName.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String rateStr = editRate.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();
        double rate = rateStr.isEmpty() ? 0.0 : Double.parseDouble(rateStr);
        int selectedPosition = contractorSpinner.getSelectedItemPosition();
        int contractorId = (selectedPosition == 0) ? -1 : contractors.get(selectedPosition - 1).getId();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        Customer customer = new Customer(customerId, name, address, rate, contractorId, notes);

        if (customerId == -1) {
            dbHelper.addCustomer(customer);
            Log.d(TAG, "Added new customer: " + name);
        } else {
            dbHelper.updateCustomer(customer);
            Log.d(TAG, "Updated customer: " + name);
        }
        setResult(RESULT_OK);
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Customer")
                .setMessage("Are you sure you want to delete this customer?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteCustomer(customerId);
                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}