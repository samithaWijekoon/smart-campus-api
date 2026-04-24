package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

 
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // Log the full exception details server-side for debugging
        // Level.SEVERE = highest priority log — this should always be investigated
        LOGGER.log(Level.SEVERE,
                "Unexpected internal server error: " + exception.getMessage(),
                exception);  // This logs the full stack trace to server logs

        // Build a SAFE, generic error response for the client
        // Never include the actual exception message or stack trace here!
        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred on the server. " +
                "Please try again later or contact the API administrator.");
        error.put("timestamp", System.currentTimeMillis());
    

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)  // HTTP 500
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
