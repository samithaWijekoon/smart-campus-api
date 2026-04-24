package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;


@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

  
    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        // Build a structured error object to return as JSON
        Map<String, Object> error = new HashMap<>();
        error.put("status", 409);
        error.put("error", "Conflict");
        error.put("message", "Room '" + exception.getRoomId() +
                "' cannot be deleted because it still has active sensors assigned to it. " +
                "Please delete or reassign all sensors in this room first.");
        error.put("roomId", exception.getRoomId());
        error.put("timestamp", System.currentTimeMillis());

        return Response
                .status(Response.Status.CONFLICT)           // HTTP 409
                .entity(error)                               // JSON body
                .type(MediaType.APPLICATION_JSON)            // Content-Type header
                .build();
    }
}
