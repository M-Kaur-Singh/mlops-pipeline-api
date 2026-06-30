package com.mlops;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class MLOpsApplication extends ResourceConfig {

    public MLOpsApplication() {
        // IMPORTANT: scan ALL packages where resources exist
        packages("resource", "model");
    }

    public static void main(String[] args) {

        URI baseUri = URI.create("http://localhost:8080/");

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                baseUri,
                new MLOpsApplication()
        );

        System.out.println("SERVER RUNNING:");
        System.out.println("http://localhost:8080/api/v1");

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            server.shutdownNow();
        }
    }
}