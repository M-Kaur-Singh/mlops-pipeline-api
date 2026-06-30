package resource;

import model.DataRepository;
import model.MachineLearningModel;
import model.MLWorkspace;
import model.LinkedWorkspaceNotFoundException; // 1. ADDED THIS IMPORT AT THE TOP

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModelResource {

    @GET
    public Collection<MachineLearningModel> getModels(@QueryParam("status") String status) {
        Collection<MachineLearningModel> allModels = DataRepository.models.values();

        // If no filter is provided, return all registered models
        if (status == null || status.trim().isEmpty()) {
            return allModels;
        }

        // Filter models matching the requested status (case-insensitive)
        List<MachineLearningModel> filteredModels = new ArrayList<>();
        for (MachineLearningModel model : allModels) {
            if (model.getStatus() != null && model.getStatus().equalsIgnoreCase(status.trim())) {
                filteredModels.add(model);
            }
        }
        return filteredModels;
    }

    @POST
    public Response registerModel(MachineLearningModel model) {
        // Validation: Verify the hosting workspace exists to avoid data orphans
        if (model.getWorkspaceId() == null || model.getWorkspaceId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"A model must be assigned to a valid workspaceId\"}")
                    .build();
        }

        MLWorkspace targetWorkspace = DataRepository.workspaces.get(model.getWorkspaceId());
        if (targetWorkspace == null) {
            // 2. THIS LINE CHOSEN TO THROW THE EXCEPTION INSTEAD OF SENDING THE OLD MANUAL JSON RESPONSE
            throw new LinkedWorkspaceNotFoundException("Target workspace with ID " + model.getWorkspaceId() + " does not exist");
        }

        // Architectural Best Practice: Server-side unique ID generation
        String generatedId = "MOD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        model.setId(generatedId);

        // Save the model to the primary map store
        DataRepository.models.put(generatedId, model);

        // Link the model's identifier back inside the workspace's array collection
        if (targetWorkspace.getModelIds() == null) {
            targetWorkspace.setModelIds(new ArrayList<>());
        }
        targetWorkspace.getModelIds().add(generatedId);

        return Response.status(Response.Status.CREATED).entity(model).build();
    }

    @Path("/{modelId}/metrics")
    public EvaluationMetricResource getEvaluationMetricResource(@PathParam("modelId") String modelId) {
        // This bridges the URL path parameter down into the sub-resource handler
        return new EvaluationMetricResource(modelId);
    }
}