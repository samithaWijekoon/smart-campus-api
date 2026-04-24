package com.smartcampus.exception;


public class SensorUnavailableException extends RuntimeException {

    /** The ID of the sensor that is unavailable */
    private final String sensorId;

    /** The current status preventing the operation (e.g., "MAINTENANCE") */
    private final String currentStatus;

    /**
     * @param sensorId      The sensor ID that cannot accept readings
     * @param currentStatus The status that is blocking the operation
     */
    public SensorUnavailableException(String sensorId, String currentStatus) {
        super("Sensor '" + sensorId + "' cannot accept readings. Current status: " + currentStatus);
        this.sensorId = sensorId;
        this.currentStatus = currentStatus;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}
