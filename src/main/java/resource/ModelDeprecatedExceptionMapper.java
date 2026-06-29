package resource;

import model.ModelDeprecatedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Provider
public class ModelDeprecatedExceptionMapper implements ExceptionMapper<ModelDeprecatedException> {

    @Override
    public Response toResponse(ModelDeprecatedException exception) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("status", Response.Status.FORBIDDEN.getStatusCode());
        errorDetails.put("error", "Forbidden");
        errorDetails.put("message", exception.getMessage());

        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDetails)
                .build();
    }
}