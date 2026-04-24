package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    
    public static final String BASE_URI = "http://0.0.0.0:8080/";

   
    public static HttpServer startServer() {
        ResourceConfig config = new ResourceConfig()
                // Auto-scan the entire com.smartcampus package for all JAX-RS components
                .packages("com.smartcampus")
                // Register Jackson for automatic POJO <-> JSON conversion
                .register(JacksonFeature.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

  
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        System.out.println("=============================================================");
        System.out.println("  Smart Campus API - 5COSC022W Coursework");
        System.out.println("=============================================================");
        System.out.println("  Server started successfully!");
        System.out.println("  Base URL  : http://localhost:8080/api/v1");
        System.out.println("  Rooms     : http://localhost:8080/api/v1/rooms");
        System.out.println("  Sensors   : http://localhost:8080/api/v1/sensors");
        System.out.println("=============================================================");
        System.out.println("  Press ENTER to stop the server...");
        System.out.println("=============================================================");

        // Block waiting for user input — server runs on background threads
        System.in.read();

        server.shutdownNow();
        LOGGER.info("Server stopped.");
    }
}
