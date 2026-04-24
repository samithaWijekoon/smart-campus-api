package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    /** The shared data store instance */
    private final DataStore store = DataStore.getInstance();

    
    private final String sensorId;

    
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // ===================================================================
    // GET /api/v1/sensors/{sensorId}/readings — Get reading history
    // ===================================================================

    @GET
    public Response getReadings() {
        // Validate the parent sensor exists before accessing readings
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' was not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // Fetch all readings for this sensor (returns empty list if none exist)
        List<SensorReading> sensorReadings = store.getReadings(sensorId);

        // Build response with metadata
        Map<String, Object> response = new HashMap<>();
        response.put("sensorId", sensorId);
        response.put("sensorType", sensor.getType());
        response.put("count", sensorReadings.size());
        response.put("readings", sensorReadings);

        return Response.ok(response).build();
    }

    // ===================================================================
    // POST /api/v1/sensors/{sensorId}/readings — Add a new reading
    // ===================================================================

    @POST
    public Response addReading(SensorReading reading) {
        // Validate the parent sensor exists
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' was not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // STATE CONSTRAINT: Only ACTIVE sensors can accept readings
        // "MAINTENANCE" and "OFFLINE" sensors cannot record data
        if (!"ACTIVE".equalsIgnoreCase(sensor.getStatus())) {
            // Throws SensorUnavailableException → mapped to 403 Forbidden
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        // Validate the reading has a value
        if (reading == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("message", "Request body must contain a reading with a 'value' field.");
            return Response.status(400).entity(error).build();
        }

        // Create a proper reading with auto-generated ID and current timestamp
        // We use the SensorReading(double value) constructor for this
        SensorReading newReading = new SensorReading(reading.getValue());

        // Store.addReading() does TWO things:
        // 1. Appends the reading to the sensor's reading history
        // 2. Updates the sensor's currentValue (SIDE EFFECT required by spec)
        store.addReading(sensorId, newReading);

        // Return 201 Created with the newly created reading
        return Response
                .status(Response.Status.CREATED)  // HTTP 201
                .entity(newReading)
                .build();
    }
}
