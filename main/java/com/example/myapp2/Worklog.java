package com.example.myapp2;

public class Worklog {
    private int id;
    private int customerId;
    private String description;
    private long clockIn;
    private Long clockOut;

    public Worklog(int id, int customerId, String description, long clockIn, Long clockOut) {
        this.id = id;
        this.customerId = customerId;
        this.description = description;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getDescription() {
        return description;
    }

    public long getClockIn() {
        return clockIn;
    }

    public Long getClockOut() {
        return clockOut;
    }

    public void setClockOut(Long clockOut) {
        this.clockOut = clockOut;
    }
}