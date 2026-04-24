package com.smartcampus.exception;


public class RoomNotEmptyException extends RuntimeException {

    /**
     * The ID of the room that has sensors assigned to it.
     */
    private final String roomId;

    /**
     * @param roomId The ID of the room that cannot be deleted
     */
    public RoomNotEmptyException(String roomId) {
        // Call RuntimeException with a descriptive message for the server logs
        super("Cannot delete room '" + roomId + "' because it still has active sensors assigned to it.");
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
