package com.example.myapp2;

public class Customer {
    private int id;
    private String name;
    private String address;
    private double rate;
    private int contractorId;
    private String notes;  // Notes field

    public Customer(int id, String name, String address, double rate, int contractorId, String notes) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rate = rate;
        this.contractorId = contractorId;
        this.notes = notes;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
    public int getContractorId() { return contractorId; }
    public void setContractorId(int contractorId) { this.contractorId = contractorId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}