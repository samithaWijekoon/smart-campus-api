package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Path("sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    /** Shared singleton data store — same instance across all requests */
    private final DataStore store = DataStore.getInstance();

    // ===================================================================
    // GET /api/v1/sensors — List all sensors (with optional type filter)
    // ===================================================================


    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(store.getSensors().values());

        // Apply type filter if provided (case-insensitive)
        if (type != null && !type.trim().isEmpty()) {
            sensorList = sensorList.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("count", sensorList.size());
        response.put("filterApplied", type != null ? type : "none");
        response.put("sensors", sensorList);

        return Response.ok(response).build();
    }

    // ===================================================================
    // POST /api/v1/sensors — Register a new sensor
    // ===================================================================

    @POST
    public Response createSensor(Sensor sensor) {
        // Validate required fields
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("message", "Sensor 'id' field is required and cannot be empty.");
            return Response.status(400).entity(error).build();
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("message", "Sensor 'roomId' field is required. Sensors must be linked to a room.");
            return Response.status(400).entity(error).build();
        }

        // Check for duplicate sensor ID
        if (store.sensorExists(sensor.getId())) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 409);
            error.put("error", "Conflict");
            error.put("message", "A sensor with ID '" + sensor.getId() + "' already exists.");
            return Response.status(409).entity(error).build();
        }

        // REFERENTIAL INTEGRITY CHECK: Verify the linked room actually exists.
        // This is the core business rule for Part 3.1.
        // If roomId is invalid, throw exception → 422 Unprocessable Entity
        if (!store.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", sensor.getRoomId());
        }

        // Default status to ACTIVE if not provided by client
        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        // Persist the sensor
        store.saveSensor(sensor);

        // Bidirectional link: update the parent room's sensorIds list
        store.getRoom(sensor.getRoomId()).addSensorId(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // ===================================================================
    // GET /api/v1/sensors/{sensorId} — Get a specific sensor
    // ===================================================================

    @GET
    @Path("{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);

        if (sensor == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' was not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(sensor).build();
    }

    // ===================================================================
    // DELETE /api/v1/sensors/{sensorId} — Delete a sensor
    // ===================================================================

    @DELETE
    @Path("{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);

        if (sensor == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' was not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // Remove this sensor from its parent room's sensorIds list
        if (sensor.getRoomId() != null && store.roomExists(sensor.getRoomId())) {
            store.getRoom(sensor.getRoomId()).removeSensorId(sensorId);
        }

        store.deleteSensor(sensorId);
        return Response.noContent().build();
    }

    // ===================================================================
    // SUB-RESOURCE LOCATOR — /api/v1/sensors/{sensorId}/readings
    // ===================================================================

    @Path("{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
