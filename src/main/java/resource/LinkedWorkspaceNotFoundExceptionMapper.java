package resource;

import model.LinkedWorkspaceNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Provider
public class LinkedWorkspaceNotFoundExceptionMapper implements ExceptionMapper<LinkedWorkspaceNotFoundException> {

    @Override
    public Response toResponse(LinkedWorkspaceNotFoundException exception) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("status", 422);
        errorDetails.put("error", "Unprocessable Entity");
        errorDetails.put("message", exception.getMessage());

        return Response.status(422) // 422 Unprocessable Entity
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDetails)
                .build();
    }
}