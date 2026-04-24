package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // Empty body — Jersey's auto-scanning does all the work.
    // All @Path and @Provider classes are discovered automatically.
}
