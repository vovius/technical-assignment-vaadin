package com.aimsio.model;

public class SignalCount {
    private int statusCount;
    private String entryDate;

    public SignalCount(int statusCount, String entryDate) {
        this.statusCount = statusCount;
        this.entryDate = entryDate;
    }

    public int getStatusCount() {
        return statusCount;
    }

    public String getEntryDate() {
        return entryDate;
    }

}
