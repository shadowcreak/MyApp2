package com.example.myapp2;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
        saveButton = findViewById(R.id.saveButton);  // Use the existing saveButton field

        List<Contractor> contractors = databaseHelper.getAllContractors();
        ArrayAdapter<Contractor> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contractors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractorSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                String address = editAddress.getText().toString();
                double rate = Double.parseDouble(editRate.getText().toString());
                String notes = editNotes.getText().toString();
                Contractor selectedContractor = (Contractor) contractorSpinner.getSelectedItem();
                int contractorId = selectedContractor.getId();

                Customer customer = new Customer(0, name, address, rate, contractorId, notes);
                databaseHelper.addCustomer(customer);

                finish();
            }
        });
    }
}