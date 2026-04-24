package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {

    // ===================================================================
    // SINGLETON IMPLEMENTATION
    // ===================================================================


    private static volatile DataStore instance;

   
    private DataStore() {
        seedSampleData();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            synchronized (DataStore.class) {
                if (instance == null) {
                    instance = new DataStore();
                }
            }
        }
        return instance;
    }

    // ===================================================================
    // DATA COLLECTIONS
    // ===================================================================

   
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

   
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();


    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    // ===================================================================
    // ROOM OPERATIONS
    // ===================================================================

    /** Returns all rooms as a collection */
    public Map<String, Room> getRooms() {
        return rooms;
    }

    /** Looks up a single room by its ID, returns null if not found */
    public Room getRoom(String id) {
        return rooms.get(id);
    }

    /** Saves a new room or updates an existing one */
    public void saveRoom(Room room) {
        rooms.put(room.getId(), room);
    }
                                                
    public void deleteRoom(String id) {
        rooms.remove(id);
    }

    /** Returns true if a room with the given ID exists */
    public boolean roomExists(String id) {
        return rooms.containsKey(id);
    }

    // ===================================================================
    // SENSOR OPERATIONS
    // ===================================================================

    /** Returns all sensors as a collection */
    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    /** Looks up a single sensor by its ID, returns null if not found */
    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    /** Saves a new sensor or updates an existing one */
    public void saveSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    /** Removes a sensor from the store */
    public void deleteSensor(String id) {
        sensors.remove(id);
    }

    /** Returns true if a sensor with the given ID exists */
    public boolean sensorExists(String id) {
        return sensors.containsKey(id);
    }

    // ===================================================================
    // SENSOR READING OPERATIONS
    // ===================================================================

    public List<SensorReading> getReadings(String sensorId) {
        // computeIfAbsent: if key doesn't exist, create an empty ArrayList
        // This is thread-safe and avoids null checks in calling code
        return readings.computeIfAbsent(sensorId, k -> new ArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {
        // Get (or create) the list for this sensor and add the new reading
        getReadings(sensorId).add(reading);

        // SIDE EFFECT: Update the parent sensor's currentValue
        // This keeps the sensor's "live" value in sync with its latest reading
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.setCurrentValue(reading.getValue());
        }
    }

    // ===================================================================
    // SAMPLE DATA — Pre-loaded for demonstration/testing
    // ===================================================================

    private void seedSampleData() {

        // --- Create Sample Rooms ---
        Room library = new Room("LIB-301", "Library Quiet Study", 50);
        Room csLab = new Room("CS-LAB-1", "Computer Science Lab 1", 30);
        Room hall = new Room("HALL-A", "Main Hall A", 200);

        // --- Create Sample Sensors ---
        Sensor tempSensor = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor co2Sensor = new Sensor("CO2-001", "CO2", "ACTIVE", 412.0, "LIB-301");
        Sensor occSensor = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 15.0, "CS-LAB-1");
        Sensor lightSensor = new Sensor("LIGHT-001", "Light", "ACTIVE", 340.0, "HALL-A");

        // Link sensors to their rooms (update the room's sensorIds list)
        library.addSensorId("TEMP-001");
        library.addSensorId("CO2-001");
        csLab.addSensorId("OCC-001");
        hall.addSensorId("LIGHT-001");

        // Save rooms
        rooms.put(library.getId(), library);
        rooms.put(csLab.getId(), csLab);
        rooms.put(hall.getId(), hall);

        // Save sensors
        sensors.put(tempSensor.getId(), tempSensor);
        sensors.put(co2Sensor.getId(), co2Sensor);
        sensors.put(occSensor.getId(), occSensor);
        sensors.put(lightSensor.getId(), lightSensor);

        // Add some sample readings for TEMP-001
        readings.put("TEMP-001", new ArrayList<>());
        readings.get("TEMP-001").add(new SensorReading("READ-001", System.currentTimeMillis() - 3600000, 21.0));
        readings.get("TEMP-001").add(new SensorReading("READ-002", System.currentTimeMillis() - 1800000, 22.0));
        readings.get("TEMP-001").add(new SensorReading("READ-003", System.currentTimeMillis(), 22.5));

        // Add some sample readings for CO2-001
        readings.put("CO2-001", new ArrayList<>());
        readings.get("CO2-001").add(new SensorReading("READ-004", System.currentTimeMillis() - 3600000, 400.0));
        readings.get("CO2-001").add(new SensorReading("READ-005", System.currentTimeMillis(), 412.0));
    }
}
