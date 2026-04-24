package com.smartcampus.exception;


public class LinkedResourceNotFoundException extends RuntimeException {

    /** The resource type that was referenced (e.g., "Room") */
    private final String resourceType;

    /** The ID that was referenced but not found (e.g., "ROOM-999") */
    private final String resourceId;

    /**
     * @param resourceType Human-readable type name (e.g., "Room")
     * @param resourceId   The ID that could not be found (e.g., "ROOM-999")
     */
    public LinkedResourceNotFoundException(String resourceType, String resourceId) {
        super("Linked resource '" + resourceType + "' with ID '" + resourceId + "' does not exist.");
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}
