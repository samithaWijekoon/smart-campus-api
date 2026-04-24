package com.smartcampus.model;


public class Sensor {

   
    private String id;

   
    private String type;

    private String status;

    private double currentValue;

   
    private String roomId;

    // ===================================================================
    // CONSTRUCTORS
    // ===================================================================

    /**
     * No-arg constructor required by Jackson for JSON deserialization.
     */
    public Sensor() {}

    /**
     * Full constructor for creating sensors with all fields initialized.
     */
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "Sensor{id='" + id + "', type='" + type + "', status='" + status +
               "', currentValue=" + currentValue + ", roomId='" + roomId + "'}";
    }
}
