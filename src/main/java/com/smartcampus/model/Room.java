package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;


public class Room {

  
    private String id;

    private String name;

    private int capacity;

    private List<String> sensorIds = new ArrayList<>();

    // ===================================================================
    // CONSTRUCTORS
    // ===================================================================

    public Room() {}

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    // ===================================================================
    // GETTERS & SETTERS
    // Jackson uses these to serialize (object->JSON) and
    // deserialize (JSON->object). All fields MUST have getters/setters.
    // ===================================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    public void addSensorId(String sensorId) {
        this.sensorIds.add(sensorId);
    }

    public void removeSensorId(String sensorId) {
        this.sensorIds.remove(sensorId);
    }

    @Override
    public String toString() {
        return "Room{id='" + id + "', name='" + name + "', capacity=" + capacity +
               ", sensorCount=" + sensorIds.size() + "}";
    }
}
