package resource;

import model.DataRepository;
import model.EvaluationMetric;
import model.MachineLearningModel;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvaluationMetricResource {

    // Global nested storage layout mapping: ModelId -> List of Evaluation Metrics
    private static final ConcurrentHashMap<String, List<EvaluationMetric>> metricsDatabase = new ConcurrentHashMap<>();

    private final String modelId;

    // Constructor captures the model context from the parent route locator
    public EvaluationMetricResource(String modelId) {
        this.modelId = modelId;
    }

    @GET
    public Response getMetricHistory() {
        MachineLearningModel parentModel = DataRepository.models.get(this.modelId);
        if (parentModel == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Parent Machine Learning Model not found\"}")
                    .build();
        }

        List<EvaluationMetric> history = metricsDatabase.getOrDefault(this.modelId, new ArrayList<>());
        return Response.ok(history).build();
    }

    @POST
    public Response appendMetric(EvaluationMetric metric) {
        MachineLearningModel parentModel = DataRepository.models.get(this.modelId);
        if (parentModel == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Parent Machine Learning Model not found\"}")
                    .build();
        }

        // Configuration setup for metric data record
        metric.setId(UUID.randomUUID().toString());
        if (metric.getTimestamp() == 0) {
            metric.setTimestamp(System.currentTimeMillis());
        }

        // Save into historical log collection list
        metricsDatabase.computeIfAbsent(this.modelId, k -> new ArrayList<>()).add(metric);

        // Side-Effect Automation: Force sync consistency to the parent model record
        parentModel.setLatestAccuracy(metric.getAccuracyScore());

        return Response.status(Response.Status.CREATED).entity(metric).build();
    }
}