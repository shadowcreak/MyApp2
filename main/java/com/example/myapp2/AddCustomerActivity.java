package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AddCustomerActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ArrayList<Contractor> contractors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        dbHelper = DatabaseHelper.getInstance(this);
        contractors = dbHelper.getAllContractors();

        Spinner spinnerContractor = findViewById(R.id.spinnerContractor);
        ArrayList<String> contractorNames = new ArrayList<>();
        contractorNames.add("Direct to Customer");
        for (Contractor contractor : contractors) {
            contractorNames.add(contractor.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contractorNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContractor.setAdapter(adapter);

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(v -> {
            EditText editTextName = findViewById(R.id.editTextName);
            EditText editTextAddress = findViewById(R.id.editTextAddress);
            EditText editTextRate = findViewById(R.id.editTextRate);
            Spinner spinnerContractorSpinner = findViewById(R.id.spinnerContractor);

            String name = editTextName.getText().toString();
            String address = editTextAddress.getText().toString();
            double rate = Double.parseDouble(editTextRate.getText().toString());
            int contractorId = spinnerContractorSpinner.getSelectedItemPosition() == 0 ? 0 : contractors.get(spinnerContractorSpinner.getSelectedItemPosition() - 1).getId();

            if (!name.isEmpty() && !address.isEmpty()) {
                Customer customer = new Customer(0, name, address, rate, contractorId);
                dbHelper.addCustomer(customer);
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}