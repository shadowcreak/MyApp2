package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class AddCustomerActivity extends AppCompatActivity {
    private static final String TAG = "AddCustomerActivity";
    private TextInputEditText editName, editAddress, editRate, editNotes;
    private Spinner contractorSpinner;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private ArrayList<Contractor> contractors;
    private RecyclerView worklogsRecyclerView;
    private WorklogAdapter worklogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editRate = findViewById(R.id.editRate);
        editNotes = findViewById(R.id.editNotes);
        contractorSpinner = findViewById(R.id.contractorSpinner);
        saveButton = findViewById(R.id.saveButton);
        worklogsRecyclerView = findViewById(R.id.worklogsRecyclerView);
        dbHelper = DatabaseHelper.getInstance(this);

        loadContractors();
        setupWorklogs();

        saveButton.setOnClickListener(v -> saveCustomer());
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

    private void setupWorklogs() {
        ArrayList<Worklog> worklogs = new ArrayList<>(); // Empty for new customers
        worklogAdapter = new WorklogAdapter(this, worklogs, dbHelper); // Added dbHelper
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

        Customer customer = new Customer(-1, name, address, rate, contractorId, notes);
        dbHelper.addCustomer(customer);
        Log.d(TAG, "Added new customer: " + name);
        setResult(RESULT_OK);
        finish();
    }
}