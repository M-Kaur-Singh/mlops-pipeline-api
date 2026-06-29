package resource;

import model.DataRepository;
import model.MLWorkspace;
import model.WorkspaceNotEmptyException; // ADDED THIS IMPORT TO FIX THE SYMBOL ERROR

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;

@Path("/workspaces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkspaceResource {

    @GET
    public Collection<MLWorkspace> getAllWorkspaces() {
        return DataRepository.workspaces.values();
    }

    @POST
    public Response createWorkspace(MLWorkspace workspace) {
        if (workspace.getId() == null || workspace.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Workspace ID cannot be empty\"}")
                    .build();
        }

        if (DataRepository.workspaces.containsKey(workspace.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Workspace already exists\"}")
                    .build();
        }

        DataRepository.workspaces.put(workspace.getId(), workspace);
        return Response.status(Response.Status.CREATED).entity(workspace).build();
    }

    @GET
    @Path("/{workspaceId}")
    public Response getWorkspaceById(@PathParam("workspaceId") String workspaceId) {
        MLWorkspace workspace = DataRepository.workspaces.get(workspaceId);
        if (workspace == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Workspace not found\"}")
                    .build();
        }
        return Response.ok(workspace).build();
    }

    @DELETE
    @Path("/{workspaceId}")
    public Response deleteWorkspace(@PathParam("workspaceId") String workspaceId) {
        MLWorkspace workspace = DataRepository.workspaces.get(workspaceId);
        if (workspace == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Workspace not found\"}")
                    .build();
        }

        // Business Logic Constraint: Throw custom exception if models are assigned
        if (workspace.getModelIds() != null && !workspace.getModelIds().isEmpty()) {
            throw new WorkspaceNotEmptyException("Cannot delete workspace '" + workspaceId + "' because it contains active deployed machine learning models.");
        }

        DataRepository.workspaces.remove(workspaceId);
        return Response.noContent().build(); // 204 No Content upon success
    }
}