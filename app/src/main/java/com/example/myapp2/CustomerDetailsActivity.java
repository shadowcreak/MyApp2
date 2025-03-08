package com.example.myapp2;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class CustomerDetailsActivity extends AppCompatActivity {
    private TextInputEditText editName, editAddress, editRate, editNotes;
    private Spinner contractorSpinner;
    private Button saveButton;
    private RecyclerView worklogsRecyclerView;
    private WorklogAdapter worklogAdapter;
    private DatabaseHelper databaseHelper;
    private int customerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        databaseHelper = DatabaseHelper.getInstance(this);

        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editRate = findViewById(R.id.editRate);
        editNotes = findViewById(R.id.editNotes);
        contractorSpinner = findViewById(R.id.contractorSpinner);
        saveButton = findViewById(R.id.saveButton);
        worklogsRecyclerView = findViewById(R.id.worklogsRecyclerView);

        worklogsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Contractor> contractors = databaseHelper.getAllContractors();
        ArrayAdapter<Contractor> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contractors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractorSpinner.setAdapter(adapter);

        customerId = getIntent().getIntExtra("customerId", -1);
        if (customerId != -1) {
            Customer customer = databaseHelper.getCustomerById(customerId);
            editName.setText(customer.getName());
            editAddress.setText(customer.getAddress());
            editRate.setText(String.valueOf(customer.getRate()));
            editNotes.setText(customer.getNotes());
            for (int i = 0; i < contractors.size(); i++) {
                if (contractors.get(i).getId() == customer.getContractorId()) {
                    contractorSpinner.setSelection(i);
                    break;
                }
            }

            List<Worklog> worklogs = databaseHelper.getWorklogsByCustomer(customerId);
            worklogAdapter = new WorklogAdapter(this, worklogs, databaseHelper); // Added databaseHelper
            worklogsRecyclerView.setAdapter(worklogAdapter);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                String address = editAddress.getText().toString();
                double rate = Double.parseDouble(editRate.getText().toString());
                String notes = editNotes.getText().toString();
                Contractor selectedContractor = (Contractor) contractorSpinner.getSelectedItem();
                int contractorId = selectedContractor.getId();

                if (customerId == -1) {
                    Customer newCustomer = new Customer(0, name, address, rate, contractorId, notes);
                    databaseHelper.addCustomer(newCustomer);
                } else {
                    Customer updatedCustomer = new Customer(customerId, name, address, rate, contractorId, notes);
                    databaseHelper.updateCustomer(updatedCustomer);
                }
                finish();
            }
        });
    }
}