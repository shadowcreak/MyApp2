package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private CustomerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper.getInstance(this);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the customer list from the database
        List<Customer> customers = databaseHelper.getAllCustomers();

        // Initialize the adapter with the correct parameters
        adapter = new CustomerAdapter(customers, databaseHelper); // Fixed line
        recyclerView.setAdapter(adapter);

        // Set up FAB click listener
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCustomerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the customer list when the activity resumes
        List<Customer> customers = databaseHelper.getAllCustomers();
        adapter = new CustomerAdapter(customers, databaseHelper); // Fixed line
        recyclerView.setAdapter(adapter);
    }
}