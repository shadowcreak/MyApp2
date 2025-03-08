package com.example.myapp2;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ManageContractorsActivity extends AppCompatActivity {
    private static final String TAG = "ManageContractorsActivity";
    private EditText editTextContractorName;
    private Button buttonAddContractor;
    private ListView listViewContractors;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> contractorNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contractors);

        editTextContractorName = findViewById(R.id.editTextContractorName);
        buttonAddContractor = findViewById(R.id.buttonAddContractor);
        listViewContractors = findViewById(R.id.listViewContractors);
        dbHelper = DatabaseHelper.getInstance(this);

        contractorNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contractorNames);
        listViewContractors.setAdapter(adapter);

        loadContractors();

        buttonAddContractor.setOnClickListener(v -> {
            String contractorName = editTextContractorName.getText().toString().trim();
            if (!contractorName.isEmpty()) {
                long newId = dbHelper.addContractor(contractorName);
                if (newId != -1) {
                    contractorNames.add(contractorName);
                    adapter.notifyDataSetChanged();
                    editTextContractorName.setText("");
                    Toast.makeText(this, "Added " + contractorName, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Added contractor: " + contractorName + " with ID: " + newId);
                } else {
                    Toast.makeText(this, "Failed to add contractor", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a contractor name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadContractors() {
        ArrayList<Contractor> contractors = dbHelper.getAllContractors();
        contractorNames.clear();
        for (Contractor contractor : contractors) {
            contractorNames.add(contractor.getName());
        }
        adapter.notifyDataSetChanged();
        Log.d(TAG, "Loaded " + contractorNames.size() + " contractors");
    }
}