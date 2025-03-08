package com.example.myapp2;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExportActivity extends AppCompatActivity {
    private static final String TAG = "ExportActivity";
    private Button buttonExport;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        buttonExport = findViewById(R.id.buttonExport);
        dbHelper = DatabaseHelper.getInstance(this);

        buttonExport.setOnClickListener(v -> exportToCsv());
    }

    private void exportToCsv() {
        try {
            ArrayList<Customer> customers = dbHelper.getAllCustomers();
            ArrayList<Worklog> worklogs = new ArrayList<>();
            for (Customer customer : customers) {
                worklogs.addAll(dbHelper.getWorklogsByCustomer(customer.getId()));
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String fileName = "CustomerWorklogs_" + timestamp + ".csv";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

            FileWriter writer = new FileWriter(file);
            writer.append("Customer ID,Name,Address,Rate,Contractor ID,Worklog ID,Description,Clock In,Clock Out\n");

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
            for (Customer customer : customers) {
                ArrayList<Worklog> customerWorklogs = dbHelper.getWorklogsByCustomer(customer.getId());
                if (customerWorklogs.isEmpty()) {
                    writer.append(String.format("%d,%s,%s,%.2f,%d,,,,\n",
                            customer.getId(), customer.getName(), customer.getAddress(), customer.getRate(), customer.getContractorId()));
                } else {
                    for (Worklog worklog : customerWorklogs) {
                        String clockOut = worklog.getClockOut() != null ? sdf.format(new Date(worklog.getClockOut())) : "";
                        writer.append(String.format("%d,%s,%s,%.2f,%d,%d,%s,%s,%s\n",
                                customer.getId(), customer.getName(), customer.getAddress(), customer.getRate(), customer.getContractorId(),
                                worklog.getId(), worklog.getDescription(), sdf.format(new Date(worklog.getClockIn())), clockOut));
                    }
                }
            }

            writer.flush();
            writer.close();
            Toast.makeText(this, "Exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "CSV exported to: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Error exporting CSV", e);
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}