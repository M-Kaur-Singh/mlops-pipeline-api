package com.mlops; // Or whichever folder it sits in

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1")
public class MLOpsApplication extends ResourceConfig {
    public MLOpsApplication() {
        // This tells Jersey to look at your folders directly
        packages("resource", "model");
    }
}