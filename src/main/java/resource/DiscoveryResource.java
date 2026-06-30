package resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Map<String, Object> getApi() {

        Map<String, Object> meta = new LinkedHashMap<>();

        meta.put("apiVersion", "v1");
        meta.put("description", "MLOps Pipeline API");
        meta.put("sysAdminContact", "admin@westminster.ac.uk");

        Map<String, String> links = new LinkedHashMap<>();
        links.put("workspaces", "/api/v1/workspaces");
        links.put("models", "/api/v1/models");

        meta.put("_links", links);

        return meta;
    }
}