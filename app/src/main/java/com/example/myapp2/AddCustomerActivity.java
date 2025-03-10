package com.example.myapp2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class AddCustomerActivity extends AppCompatActivity {
    private TextInputEditText editName, editAddress, editRate, editNotes;
    private Spinner contractorSpinner;
    private Button saveButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        databaseHelper = DatabaseHelper.getInstance(this);

        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editRate = findViewById(R.id.editRate);
        editNotes = findViewById(R.id.editNotes);
        contractorSpinner = findViewById(R.id.contractorSpinner);
        saveButton = findViewById(R.id.saveButton);

        List<Contractor> contractors = databaseHelper.getAllContractors();
        ArrayAdapter<Contractor> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contractors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractorSpinner.setAdapter(adapter);

        if (contractors.isEmpty()) {
            Toast.makeText(this, "No contractors available. Please add a contractor first.", Toast.LENGTH_LONG).show();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = editName.getText().toString().trim();
                    String address = editAddress.getText().toString().trim();
                    String rateText = editRate.getText().toString().trim();
                    String notes = editNotes.getText().toString().trim();

                    if (name.isEmpty() || rateText.isEmpty()) {
                        Toast.makeText(AddCustomerActivity.this, "Name and Rate are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double rate = Double.parseDouble(rateText);
                    Contractor selectedContractor = (Contractor) contractorSpinner.getSelectedItem();
                    if (selectedContractor == null) {
                        Toast.makeText(AddCustomerActivity.this, "Please select a contractor", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int contractorId = selectedContractor.getId();

                    Customer newCustomer = new Customer(0, name, address, rate, contractorId, notes);
                    databaseHelper.addCustomer(newCustomer);
                    Log.d("AddCustomerActivity", "Added customer: " + name);
                    Toast.makeText(AddCustomerActivity.this, "Customer added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (NumberFormatException e) {
                    Toast.makeText(AddCustomerActivity.this, "Invalid rate format", Toast.LENGTH_SHORT).show();
                    Log.e("AddCustomerActivity", "Error adding customer: " + e.getMessage());
                } catch (Exception e) {
                    Toast.makeText(AddCustomerActivity.this, "Error adding customer", Toast.LENGTH_SHORT).show();
                    Log.e("AddCustomerActivity", "Error adding customer: " + e.getMessage());
                }
            }
        });
    }
}