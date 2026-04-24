package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;


@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 403);
        error.put("error", "Forbidden");
        error.put("message", "Sensor '" + exception.getSensorId() +
                "' is currently in '" + exception.getCurrentStatus() + "' state and cannot accept new readings. " +
                "Only sensors with status 'ACTIVE' can record readings.");
        error.put("sensorId", exception.getSensorId());
        error.put("currentStatus", exception.getCurrentStatus());
        error.put("timestamp", System.currentTimeMillis());

        return Response
                .status(Response.Status.FORBIDDEN)   // HTTP 403
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
