package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;


@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 422);
        error.put("error", "Unprocessable Entity");
        error.put("message", "The referenced " + exception.getResourceType() +
                " with ID '" + exception.getResourceId() + "' does not exist. " +
                "Please ensure the linked resource exists before creating this entity.");
        error.put("resourceType", exception.getResourceType());
        error.put("resourceId", exception.getResourceId());
        error.put("timestamp", System.currentTimeMillis());

        // 422 doesn't have a named constant in JAX-RS Response.Status enum
        // so we use the integer code directly
        return Response
                .status(422)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
