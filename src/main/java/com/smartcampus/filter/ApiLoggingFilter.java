package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * =====================================================================
 * API OBSERVABILITY FILTER — Logs every request and response
 * =====================================================================
 *
 * This single class implements BOTH filter interfaces:
 *   - ContainerRequestFilter:  runs BEFORE the resource method executes
 *   - ContainerResponseFilter: runs AFTER the resource method executes
 *
 * WHY USE FILTERS FOR LOGGING INSTEAD OF MANUAL Logger.info() CALLS?
 * -------------------------------------------------------------------
 * Logging is a "cross-cutting concern" — it applies to every endpoint
 * uniformly, not just specific business logic. Using filters means:
 *
 * 1. DRY (Don't Repeat Yourself): One class handles logging for ALL
 *    endpoints instead of copy-pasting Logger calls into each resource.
 *
 * 2. Separation of Concerns: Resource classes focus purely on business
 *    logic. Logging infrastructure is kept separate.
 *
 * 3. Consistency: Every request/response is logged identically.
 *    No risk of a developer forgetting to add logging to a new endpoint.
 *
 * 4. Easy to toggle: To disable all API logging, just remove @Provider
 *    or unregister this class — no need to touch resource classes.
 *
 * 5. Aspect-Oriented: Filters can be chained and ordered, allowing
 *    multiple concerns (logging, auth, CORS) to be layered cleanly.
 *
 * The @Provider annotation tells JAX-RS to auto-discover and register
 * this filter for all requests/responses automatically.
 */
@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Standard Java logger — outputs to the server console.
     * Using the class name as the logger name follows best practice:
     * it makes it easy to filter log output by class in log management tools.
     */
    private static final Logger LOGGER = Logger.getLogger(ApiLoggingFilter.class.getName());

    /**
     * =====================================================================
     * REQUEST FILTER — Called BEFORE the resource method executes
     * =====================================================================
     *
     * Logs the incoming HTTP method and full request URI.
     * Example log output: "→ Incoming Request: GET /api/v1/rooms"
     *
     * @param requestContext Contains all request details (method, URI, headers, body)
     * @throws IOException Required by the interface contract
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // getMethod() returns "GET", "POST", "DELETE", etc.
        String method = requestContext.getMethod();

        // getUriInfo().getRequestUri() returns the full URI including query params
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        // Log the incoming request with → prefix for easy visual scanning
        LOGGER.info("→ Incoming Request: " + method + " " + uri);
    }

    
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        // getStatus() returns the numeric HTTP status code (200, 201, 404, etc.)
        int statusCode = responseContext.getStatus();

        // getStatusInfo() provides the reason phrase ("OK", "Created", "Not Found", etc.)
        String statusPhrase = responseContext.getStatusInfo().getReasonPhrase();

        // Log the outgoing response with ← prefix for easy visual scanning
        LOGGER.info("← Outgoing Response: " + statusCode + " " + statusPhrase);
    }
}
