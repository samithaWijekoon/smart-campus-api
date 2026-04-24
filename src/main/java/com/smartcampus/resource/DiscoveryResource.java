package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


@Path("")
public class DiscoveryResource {

 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiInfo() {

        // Build the links map (HATEOAS navigation)
        // Maps resource names to their URLs so clients can navigate
        Map<String, String> links = new HashMap<>();
        links.put("rooms",    "/api/v1/rooms");
        links.put("sensors",  "/api/v1/sensors");

        // Build the full response object
        Map<String, Object> response = new HashMap<>();
        response.put("api",         "Smart Campus Sensor & Room Management API");
        response.put("version",     "1.0.0");
        response.put("description", "RESTful API for managing campus rooms, sensors, and sensor readings");
        response.put("contact",     "admin@smartcampus.westminster.ac.uk");
        response.put("module",      "5COSC022W - Client-Server Architectures");
        response.put("baseUrl",     "/api/v1");
        response.put("resources",   links);
        response.put("timestamp",   System.currentTimeMillis());

        // Return 200 OK with the metadata JSON
        return Response.ok(response).build();
    }
}
