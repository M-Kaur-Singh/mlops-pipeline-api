package resource;

import model.WorkspaceNotEmptyException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Provider
public class WorkspaceNotEmptyExceptionMapper implements ExceptionMapper<WorkspaceNotEmptyException> {

    @Override
    public Response toResponse(WorkspaceNotEmptyException exception) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("status", Response.Status.CONFLICT.getStatusCode());
        errorDetails.put("error", "Resource Conflict");
        errorDetails.put("message", exception.getMessage());

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDetails)
                .build();
    }
}