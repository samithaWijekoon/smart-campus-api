package com.smartcampus.model;

import java.util.UUID;

public class SensorReading {


    private String id;

    private long timestamp;

    private double value;

    // ===================================================================
    // CONSTRUCTORS
    // ===================================================================

    public SensorReading() {}

    public SensorReading(double value) {
        // UUID.randomUUID() generates a cryptographically random unique ID
        this.id = UUID.randomUUID().toString();
        // System.currentTimeMillis() gives current time as Unix epoch in ms
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }

    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // ===================================================================
    // GETTERS & SETTERS
    // ===================================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SensorReading{id='" + id + "', timestamp=" + timestamp + ", value=" + value + "}";
    }
}
