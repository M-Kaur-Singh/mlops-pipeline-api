package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;

@Path("/api/v1/workspaces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkspaceResource {

    private static Map<String, String> db = new HashMap<>();

    @GET
    public Response getAll() {
        return Response.ok(db.values()).build();
    }

    @POST
    public Response create(Map<String, String> workspace) {

        String id = workspace.get("id");

        if (id == null || id.isEmpty()) {
            return Response.status(400)
                    .entity("{\"error\":\"Workspace ID required\"}")
                    .build();
        }

        db.put(id, id);

        return Response.status(201).entity(workspace).build();
    }

    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") String id) {

        if (!db.containsKey(id)) {
            return Response.status(404)
                    .entity("{\"error\":\"Not found\"}")
                    .build();
        }

        return Response.ok(db.get(id)).build();
    }
}