package com.example.myapp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CustomerDB";
    private static final int DATABASE_VERSION = 6;  // Incremented for 'notes' column

    // Customer table
    private static final String TABLE_CUSTOMERS = "customers";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_RATE = "rate";
    private static final String COLUMN_CONTRACTOR_ID = "contractor_id";
    private static final String COLUMN_NOTES = "notes";  // New column for notes

    // Contractor table
    private static final String TABLE_CONTRACTORS = "contractors";

    // Worklog table
    private static final String TABLE_WORKLOGS = "worklogs";
    private static final String COLUMN_WORKLOG_ID = "id";
    private static final String COLUMN_CUSTOMER_ID = "customer_id";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CLOCK_IN = "clock_in";
    private static final String COLUMN_CLOCK_OUT = "clock_out";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CUSTOMERS_TABLE = "CREATE TABLE " + TABLE_CUSTOMERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_RATE + " REAL,"
                + COLUMN_CONTRACTOR_ID + " INTEGER,"
                + COLUMN_NOTES + " TEXT)";  // Added notes column
        db.execSQL(CREATE_CUSTOMERS_TABLE);

        String CREATE_CONTRACTORS_TABLE = "CREATE TABLE " + TABLE_CONTRACTORS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT)";
        db.execSQL(CREATE_CONTRACTORS_TABLE);

        String CREATE_WORKLOGS_TABLE = "CREATE TABLE " + TABLE_WORKLOGS + "("
                + COLUMN_WORKLOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CUSTOMER_ID + " INTEGER,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_CLOCK_IN + " INTEGER,"
                + COLUMN_CLOCK_OUT + " INTEGER)";
        db.execSQL(CREATE_WORKLOGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + TABLE_CUSTOMERS + " ADD COLUMN " + COLUMN_NOTES + " TEXT");
        }
    }

    public void addCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, customer.getName());
        values.put(COLUMN_ADDRESS, customer.getAddress());
        values.put(COLUMN_RATE, customer.getRate());
        values.put(COLUMN_CONTRACTOR_ID, customer.getContractorId());
        values.put(COLUMN_NOTES, customer.getNotes());
        long newRowId = db.insert(TABLE_CUSTOMERS, null, values);
        if (newRowId != -1) {
            customer.setId((int) newRowId);
        }
        db.close();
    }

    public void updateCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, customer.getName());
        values.put(COLUMN_ADDRESS, customer.getAddress());
        values.put(COLUMN_RATE, customer.getRate());
        values.put(COLUMN_CONTRACTOR_ID, customer.getContractorId());
        values.put(COLUMN_NOTES, customer.getNotes());
        db.update(TABLE_CUSTOMERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(customer.getId())});
        db.close();
    }

    public void deleteCustomer(int customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORKLOGS, COLUMN_CUSTOMER_ID + "=?", new String[]{String.valueOf(customerId)});
        db.delete(TABLE_CUSTOMERS, COLUMN_ID + "=?", new String[]{String.valueOf(customerId)});
        db.close();
    }

    public Customer getCustomerById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOMERS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_ADDRESS, COLUMN_RATE, COLUMN_CONTRACTOR_ID, COLUMN_NOTES},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Customer customer = new Customer(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONTRACTOR_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))
            );
            cursor.close();
            return customer;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMERS, null);
        if (cursor.moveToFirst()) {
            do {
                customers.add(new Customer(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONTRACTOR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return customers;
    }

    public ArrayList<Customer> getCustomersByContractor(int contractorId) {
        ArrayList<Customer> customers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOMERS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_ADDRESS, COLUMN_RATE, COLUMN_CONTRACTOR_ID, COLUMN_NOTES},
                COLUMN_CONTRACTOR_ID + "=?", new String[]{String.valueOf(contractorId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                customers.add(new Customer(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONTRACTOR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return customers;
    }

    public ArrayList<Contractor> getAllContractors() {
        ArrayList<Contractor> contractors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTRACTORS, null);
        if (cursor.moveToFirst()) {
            do {
                contractors.add(new Contractor(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contractors;
    }

    public long addContractor(String contractorName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contractorName);
        long newRowId = db.insert(TABLE_CONTRACTORS, null, values);
        Log.d("DatabaseHelper", "Added contractor: " + contractorName + " with ID: " + newRowId);
        db.close();
        return newRowId;
    }

    public long addWorklog(Worklog worklog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_ID, worklog.getCustomerId());
        values.put(COLUMN_DESCRIPTION, worklog.getDescription());
        values.put(COLUMN_CLOCK_IN, worklog.getClockIn());
        if (worklog.getClockOut() != null) {
            values.put(COLUMN_CLOCK_OUT, worklog.getClockOut());
        } else {
            values.putNull(COLUMN_CLOCK_OUT);
        }
        long newRowId = db.insert(TABLE_WORKLOGS, null, values);
        Log.d("DatabaseHelper", "Added worklog with ID: " + newRowId);
        if (newRowId != -1) {
            worklog.setId((int) newRowId);
        }
        return newRowId;
    }

    public void updateWorklog(Worklog worklog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_ID, worklog.getCustomerId());
        values.put(COLUMN_DESCRIPTION, worklog.getDescription());
        values.put(COLUMN_CLOCK_IN, worklog.getClockIn());
        if (worklog.getClockOut() != null) {
            values.put(COLUMN_CLOCK_OUT, worklog.getClockOut());
        } else {
            values.putNull(COLUMN_CLOCK_OUT);
        }
        db.update(TABLE_WORKLOGS, values, COLUMN_WORKLOG_ID + "=?", new String[]{String.valueOf(worklog.getId())});
        db.close();
    }

    public ArrayList<Worklog> getWorklogsByCustomer(int customerId) {
        ArrayList<Worklog> worklogs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKLOGS, new String[]{COLUMN_WORKLOG_ID, COLUMN_CUSTOMER_ID, COLUMN_DESCRIPTION, COLUMN_CLOCK_IN, COLUMN_CLOCK_OUT},
                COLUMN_CUSTOMER_ID + "=?", new String[]{String.valueOf(customerId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Long clockOut = cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_CLOCK_OUT)) ? null : cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CLOCK_OUT));
                worklogs.add(new Worklog(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKLOG_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CLOCK_IN)),
                        clockOut
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return worklogs;
    }
}