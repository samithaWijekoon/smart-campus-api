package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("rooms")
@Produces(MediaType.APPLICATION_JSON)   // All responses are JSON by default
@Consumes(MediaType.APPLICATION_JSON)   // All request bodies must be JSON
public class RoomResource {

   
    private final DataStore store = DataStore.getInstance();


    @GET
    public Response getAllRooms() {
        // Convert the Map values to a List for cleaner JSON array output
        List<Room> roomList = new ArrayList<>(store.getRooms().values());

        // Build a response wrapper with metadata
        Map<String, Object> response = new HashMap<>();
        response.put("count", roomList.size());
        response.put("rooms", roomList);

        return Response.ok(response).build();
    }

    // ===================================================================
    // POST /api/v1/rooms — Create a new room
    // ===================================================================

    @POST
    public Response createRoom(Room room) {
        // --- Input Validation ---
        // Check that required fields are present
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("message", "Room 'id' field is required and cannot be empty.");
            return Response.status(400).entity(error).build();
        }

        if (room.getName() == null || room.getName().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("message", "Room 'name' field is required and cannot be empty.");
            return Response.status(400).entity(error).build();
        }

        // Check for duplicate ID — don't overwrite existing rooms silently
        if (store.roomExists(room.getId())) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 409);
            error.put("error", "Conflict");
            error.put("message", "A room with ID '" + room.getId() + "' already exists.");
            return Response.status(409).entity(error).build();
        }

        // Ensure the sensorIds list is initialized (not null)
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        // Save the room to the data store
        store.saveRoom(room);

        // Return 201 Created with the newly created room in the body
        // REST convention: POST should return 201 with the created resource
        return Response
                .status(Response.Status.CREATED)  // HTTP 201
                .entity(room)
                .build();
    }

    // ===================================================================
    // GET /api/v1/rooms/{roomId} — Get a specific room
    // ===================================================================

    @GET
    @Path("{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);

        // If room not found, return 404 with an informative message
        if (room == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Room with ID '" + roomId + "' was not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(room).build();
    }

    // ===================================================================
    // DELETE /api/v1/rooms/{roomId} — Delete a room
    // ===================================================================

    @DELETE
    @Path("{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        // Check if the room exists
        Room room = store.getRoom(roomId);

        if (room == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Room with ID '" + roomId + "' was not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // BUSINESS LOGIC: Check if the room has sensors
        // sensorIds list stores the IDs of all sensors in this room
        if (!room.getSensorIds().isEmpty()) {
            // Throw our custom exception — the RoomNotEmptyExceptionMapper
            // will intercept this and return a clean 409 JSON response
            throw new RoomNotEmptyException(roomId);
        }

        // Safe to delete — room has no sensors
        store.deleteRoom(roomId);

        // 204 No Content: successful deletion, no body needed
        // REST convention: DELETE should return 204 on success
        return Response.noContent().build();
    }
}
