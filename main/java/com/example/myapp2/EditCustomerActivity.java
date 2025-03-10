package com.example.myapp2;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class EditCustomerActivity extends AppCompatActivity {
    private TextInputEditText editName, editAddress, editRate, editNotes;  // Added editNotes
    private Spinner contractorSpinner;
    private Button saveButton;
    private DatabaseHelper databaseHelper;
    private int customerId = -1;  // -1 means new customer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        // Initialize database helper
        databaseHelper = DatabaseHelper.getInstance(this);

        // Link UI elements
        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editRate = findViewById(R.id.editRate);
        editNotes = findViewById(R.id.editNotes);  // New notes field
        contractorSpinner = findViewById(R.id.contractorSpinner);
        saveButton = findViewById(R.id.saveButton);

        // Set up contractor spinner
        List<Contractor> contractors = databaseHelper.getAllContractors();
        ArrayAdapter<Contractor> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contractors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractorSpinner.setAdapter(adapter);

        // Check if editing an existing customer
        customerId = getIntent().getIntExtra("customerId", -1);
        if (customerId != -1) {
            Customer customer = databaseHelper.getCustomerById(customerId);
            editName.setText(customer.getName());
            editAddress.setText(customer.getAddress());
            editRate.setText(String.valueOf(customer.getRate()));
            editNotes.setText(customer.getNotes());  // Load notes
            // Set spinner to correct contractor
            for (int i = 0; i < contractors.size(); i++) {
                if (contractors.get(i).getId() == customer.getContractorId()) {
                    contractorSpinner.setSelection(i);
                    break;
                }
            }
        }

        // Save button click handler
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from fields
                String name = editName.getText().toString();
                String address = editAddress.getText().toString();
                double rate = Double.parseDouble(editRate.getText().toString());
                String notes = editNotes.getText().toString();  // Get notes
                Contractor selectedContractor = (Contractor) contractorSpinner.getSelectedItem();
                int contractorId = selectedContractor.getId();

                if (customerId == -1) {
                    // Add new customer
                    Customer newCustomer = new Customer(0, name, address, rate, contractorId, notes);
                    databaseHelper.addCustomer(newCustomer);
                } else {
                    // Update existing customer
                    Customer updatedCustomer = new Customer(customerId, name, address, rate, contractorId, notes);
                    databaseHelper.updateCustomer(updatedCustomer);
                }
                finish();  // Close activity
            }
        });
    }
}