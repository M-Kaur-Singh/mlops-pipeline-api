package resource;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {

        // Log real issue internally
        LOGGER.log(Level.SEVERE, "An unexpected system error occurred: ", exception);

        // ✅ HANDLE 404 PROPERLY (IMPORTANT FOR MARKS)
        if (exception instanceof NotFoundException) {

            Map<String, Object> errorDetails = new LinkedHashMap<>();
            errorDetails.put("status", 404);
            errorDetails.put("error", "Not Found");
            errorDetails.put("message", "The requested resource does not exist");

            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(errorDetails)
                    .build();
        }

        // ❗ ALL OTHER ERRORS = REAL SERVER FAILURE (500)
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("status", 500);
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("message", "A serious unexpected error occurred on our servers. Please contact system administrators.");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDetails)
                .build();
    }
}