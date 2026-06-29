package resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getDiscoveryMetadata() {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("apiVersion", "v1");
        metadata.put("description", "MLOps Production Workspace & Model Management Pipeline API");
        metadata.put("sysAdminContact", "lead-backend-architect@westminster.ac.uk");

        Map<String, String> collectionLinks = new LinkedHashMap<>();
        collectionLinks.put("workspaces", "/api/v1/workspaces");
        collectionLinks.put("models", "/api/v1/models");
        metadata.put("_links", collectionLinks);

        return metadata;
    }
}