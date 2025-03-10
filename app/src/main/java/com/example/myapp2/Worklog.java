package com.example.myapp2;

public class Worklog {
    private int id;
    private int customerId;
    private long clockIn;
    private Long clockOut;
    private String description;

    public Worklog(int id, int customerId, long clockIn, Long clockOut, String description) {
        this.id = id;
        this.customerId = customerId;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public long getClockIn() { return clockIn; }
    public Long getClockOut() { return clockOut; }
    public String getDescription() { return description; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setClockIn(long clockIn) { this.clockIn = clockIn; }
    public void setClockOut(Long clockOut) { this.clockOut = clockOut; }
    public void setDescription(String description) { this.description = description; }
}