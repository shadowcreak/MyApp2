package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class EditCustomerActivity extends AppCompatActivity {
    private static final String TAG = "EditCustomerActivity";
    private EditText editTextName, editTextAddress;
    private Spinner spinnerContractor;
    private Button buttonSave, buttonDelete;
    private DatabaseHelper dbHelper;
    private int customerId = -1;
    private ArrayList<Contractor> contractors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        spinnerContractor = findViewById(R.id.spinnerContractor);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
        dbHelper = DatabaseHelper.getInstance(this);

        Intent intent = getIntent();
        customerId = intent.getIntExtra("CUSTOMER_ID", -1);

        loadContractors();
        if (customerId != -1) {
            loadCustomerData();
            buttonDelete.setVisibility(View.VISIBLE);
        } else {
            buttonDelete.setVisibility(View.GONE);
        }

        buttonSave.setOnClickListener(v -> saveCustomer());
        buttonDelete.setOnClickListener(v -> confirmDelete());
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
        spinnerContractor.setAdapter(adapter);
    }

    private void loadCustomerData() {
        Customer customer = dbHelper.getCustomerById(customerId);
        if (customer != null) {
            editTextName.setText(customer.getName());
            editTextAddress.setText(customer.getAddress());
            int contractorId = customer.getContractorId();
            if (contractorId == -1) {
                spinnerContractor.setSelection(0);
            } else {
                for (int i = 0; i < contractors.size(); i++) {
                    if (contractors.get(i).getId() == contractorId) {
                        spinnerContractor.setSelection(i + 1);
                        break;
                    }
                }
            }
        }
    }

    private void saveCustomer() {
        String name = editTextName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        int selectedPosition = spinnerContractor.getSelectedItemPosition();
        int contractorId = (selectedPosition == 0) ? -1 : contractors.get(selectedPosition - 1).getId();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use existing rate if editing, 0.0 if new
        double rate = 0.0;
        if (customerId != -1) {
            Customer existingCustomer = dbHelper.getCustomerById(customerId);
            if (existingCustomer != null) {
                rate = existingCustomer.getRate();
            }
        }
        Customer customer = new Customer(customerId, name, address, rate, contractorId);

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
        Customer customer = dbHelper.getCustomerById(customerId);
        if (customer != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Customer")
                    .setMessage("Are you sure you want to delete " + customer.getName() + "? This will also delete all associated worklogs.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.deleteCustomer(customerId);
                        Toast.makeText(this, "Deleted " + customer.getName(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .setCancelable(false)
                    .show();
        }
    }
}