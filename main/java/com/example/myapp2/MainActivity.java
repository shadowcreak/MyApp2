package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Canvas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Spinner spinnerContractor;
    private RecyclerView recyclerViewCustomers;
    private FloatingActionButton fabAddCustomer;
    private Button buttonManageContractors;
    private Button buttonExportCsv;
    private CustomerAdapter adapter;
    private ArrayList<Customer> customers;
    private DatabaseHelper dbHelper;
    private ArrayList<Contractor> contractors;
    private static final int DIRECT_TO_CUSTOMER_POSITION = 0;
    private ActivityResultLauncher<Intent> addCustomerLauncher;
    private boolean hasLoadedCustomers = false;
    private boolean isSpinnerInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        try {
            spinnerContractor = findViewById(R.id.spinnerContractor);
            recyclerViewCustomers = findViewById(R.id.recyclerViewCustomers);
            fabAddCustomer = findViewById(R.id.fabAddCustomer);
            buttonManageContractors = findViewById(R.id.buttonManageContractors);
            buttonExportCsv = findViewById(R.id.buttonExportCsv);
            dbHelper = DatabaseHelper.getInstance(this);
            customers = new ArrayList<>();
            adapter = new CustomerAdapter(this, customers) {
                @Override
                public void onBindViewHolder(CustomerViewHolder holder, int position) {
                    super.onBindViewHolder(holder, position);
                    Customer customer = customers.get(position);
                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, EditCustomerActivity.class);
                        intent.putExtra("CUSTOMER_ID", customer.getId());
                        startActivityForResult(intent, 2);
                    });
                }
            };
            recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewCustomers.setAdapter(adapter);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToActionCallback());
            itemTouchHelper.attachToRecyclerView(recyclerViewCustomers);

            addCustomerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            hasLoadedCustomers = false;
                            loadCustomers();
                            Log.d(TAG, "After addCustomerLauncher, customers size: " + customers.size());
                            Toast.makeText(this, "Customer added, loaded " + customers.size() + " customers", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            loadContractors();
            loadCustomers();

            fabAddCustomer.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddCustomerActivity.class);
                addCustomerLauncher.launch(intent);
            });

            buttonManageContractors.setOnClickListener(v -> {
                startActivity(new Intent(this, ManageContractorsActivity.class));
            });

            buttonExportCsv.setOnClickListener(v -> {
                startActivity(new Intent(this, ExportActivity.class));
            });

            spinnerContractor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!isSpinnerInitialized) {
                        Log.d(TAG, "Spinner initial selection, skipping loadCustomers");
                        isSpinnerInitialized = true;
                        return;
                    }
                    Log.d(TAG, "Spinner onItemSelected, position: " + position + ", customers size before load: " + customers.size());
                    loadCustomers();
                    Log.d(TAG, "Spinner onItemSelected, position: " + position + ", customers size after load: " + customers.size());
                    Toast.makeText(MainActivity.this, "Selected position: " + position + ", Loaded " + customers.size() + " customers", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } catch (Exception e) {
            Log.e(TAG, "Crash in onCreate", e);
            Toast.makeText(this, "App crashed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called, customers size: " + customers.size());
        if (!hasLoadedCustomers) {
            loadCustomers();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            hasLoadedCustomers = false;
            loadCustomers();
            Toast.makeText(this, "Customer updated, loaded " + customers.size() + " customers", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadContractors() {
        try {
            contractors = dbHelper.getAllContractors();
            ArrayList<String> contractorNames = new ArrayList<>();
            contractorNames.add("Direct to Customer");
            for (Contractor contractor : contractors) {
                contractorNames.add(contractor.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contractorNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerContractor.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error loading contractors", e);
            Toast.makeText(this, "Failed to load contractors", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCustomers() {
        if (hasLoadedCustomers) {
            Log.d(TAG, "Skipping redundant loadCustomers call, customers size: " + customers.size());
            return;
        }
        hasLoadedCustomers = true;

        try {
            int selectedPosition = spinnerContractor.getSelectedItemPosition();
            Log.d(TAG, "Loading customers for position: " + selectedPosition + ", customers size before: " + customers.size());
            customers.clear();
            Log.d(TAG, "After clear, customers size: " + customers.size());
            if (selectedPosition == DIRECT_TO_CUSTOMER_POSITION) {
                customers.addAll(dbHelper.getAllCustomers());
                Log.d(TAG, "Added all customers, size: " + customers.size());
            } else if (selectedPosition > DIRECT_TO_CUSTOMER_POSITION && selectedPosition - 1 < contractors.size()) {
                int contractorId = contractors.get(selectedPosition - 1).getId();
                customers.addAll(dbHelper.getCustomersByContractor(contractorId));
                Log.d(TAG, "Added customers for contractor ID: " + contractorId + ", size: " + customers.size());
            }
            Log.d(TAG, "Before adapter updateData, customers size: " + customers.size());
            adapter.updateData(customers);
            Log.d(TAG, "After adapter updateData, customers size: " + customers.size());
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Adapter notified, final customers size: " + customers.size());
            recyclerViewCustomers.getRecycledViewPool().clear();
            recyclerViewCustomers.invalidate();
        } catch (Exception e) {
            Log.e(TAG, "Error loading customers", e);
            Toast.makeText(this, "Failed to load customers", Toast.LENGTH_SHORT).show();
        }
    }

    private class SwipeToActionCallback extends ItemTouchHelper.SimpleCallback {
        private static final float ALPHA_FULL = 1.0f;

        public SwipeToActionCallback() {
            super(0, ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (position >= 0 && position < customers.size()) {
                Customer customer = adapter.getCustomerAt(position);
                if (direction == ItemTouchHelper.RIGHT) {
                    Intent intent = new Intent(MainActivity.this, CustomerDetailsActivity.class);
                    intent.putExtra("CUSTOMER_ID", customer.getId());
                    startActivity(intent);
                    adapter.notifyItemChanged(position);
                }
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            float width = (float) itemView.getWidth();

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                itemView.setTranslationX(dX);
                itemView.setAlpha(ALPHA_FULL - Math.abs(dX) / (width / 2));
            } else {
                itemView.setTranslationX(0);
                itemView.setAlpha(ALPHA_FULL);
            }
        }
    }
}