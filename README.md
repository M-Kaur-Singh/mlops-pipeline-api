# MLOps Pipeline Management API Report

## 1. Overview of API Design
**Answer:** This project implements a RESTful API built using JAX-RS (Jersey implementation) and Java with an embedded Grizzly HTTP server to manage machine learning operations (MLOps) pipelines. The API provides a decoupled architecture to manage Workspaces, Machine Learning Models, and evaluation metrics in a structured and scalable way.

The system follows a resource-based design:

* Workspace: Represents a logical boundary for a data science team, containing deployed models and associated resources.
* MachineLearningModel: Represents individual machine learning models, tracking framework, status, and performance metrics, and is linked to a workspace.
* Evaluation Metrics: Represents historical performance data for models, enabling tracking of accuracy over time and supporting monitoring of model behaviour.
---

## 2. Step-by-Step Instructions

### Prerequisites

* Java Development Kit (JDK) 17 or higher
* Apache Maven 3.8.x or higher
* IntelliJ IDEA (or any Java IDE) with terminal support

Step 1: Build the Project

To clean previous builds, compile the project, run tests, and package the application into a runnable JAR, execute the following command from the project root directory:
```
mvn clean package
```

Step 2: Run the Server

Start the embedded Grizzly HTTP server by running the main application class (MLOpsApplication).

You can either:

* Run the MLOpsApplication main method directly from IntelliJ IDEA
OR
* Use Maven:
```
mvn exec:java
```
API Base URL
Once the server is running, the API will be available at:

```
http://localhost:8080/api/v1 
```
---

### 3. Sample cURL Commands

# 1. Create Workspace (POST)
```bash
curl -X POST http://localhost:8080/api/v1/workspaces \
  -H "Content-Type: application/json" \
  -d '{"teamName": "Production Analytics", "storageQuotaGb": 100}'
  ```

# 2. Get All Workspaces (GET)
```
curl -X GET http://localhost:8080/api/v1/workspaces
```

# 3. Get Workspace by ID (GET)
```
curl -X GET http://localhost:8080/api/v1/workspaces/WS-1
```

# 4. Create Model (POST)
```
curl -X POST http://localhost:8080/api/v1/models \
  -H "Content-Type: application/json" \
  -d '{"framework": "Scikit-Learn", "status": "TRAINING", "workspaceId": "WS-1", "latestAccuracy": 0.0}'
```

# 5. Get All Models (GET)
```
curl -X GET http://localhost:8080/api/v1/models
```
---

## 4. Conceptual Report: Coursework Answers

## PART 1: SETUP & DISCOVERY

### 1.1 Architecture & Config Question
When returning a Java object from a method, it is automatically serialized into JSON. Explain the role of a MessageBodyWriter or a JSON provider (like Jackson) in this conversion process.

**Answer:** A MessageBodyWriter is a JAX-RS component responsible for converting Java objects (POJOs) into the format required for an HTTP response. When a resource method returns a Java object and is annotated with @Produces(MediaType.APPLICATION_JSON), the JAX-RS runtime selects a suitable MessageBodyWriter to perform the conversion. A JSON provider such as Jackson is a common implementation of this functionality. Jackson inspects the object's properties (typically through its getter methods) and serializes them into valid JSON, which is then written to the HTTP response body and sent to the client.

### 1.2 Discovery Endpoint Question
REST architecture dictates that APIs should be strictly 'stateless'. Define what statelessness means in this context and explain why it makes cloud APIs easier to scale horizontally across multiple servers.

**Answer:**  In RESTful system design, statelessness means that the server does not store any client session state between requests. Each HTTP request must be self-contained and include all the information required to process it, such as authentication details, request parameters, and resource identifiers. The server treats each request independently, without relying on information from previous interactions.

This requirement significantly simplifies horizontal scaling in cloud environments. Because individual server instances do not retain client session data in memory, a load balancer can distribute consecutive requests from the same client across different servers without affecting system behaviour. As each request contains all necessary information, there is no need to synchronise session state between servers or implement complex session replication mechanisms. This improves scalability, reliability, and fault tolerance.

---

## PART 2: WORKSPACE MANAGEMENT

### 2.1 Workspace Resource Implementation Question
Discuss how implementing HTTP Cache-Control headers on the GET workspaces endpoint could improve performance for the client and reduce unnecessary processing load on the server.

**Answer:** Appending HTTP Cache-Control headers (for example, Cache-Control: max-age=30) allows the client or intermediary caches to store the response from the GET /workspaces endpoint for a specified period. This improves performance for the client because cached responses can be returned immediately without making another request to the server, reducing network latency and improving response times. It also reduces unnecessary processing on the server because repeated requests for unchanged workspace data can be served from the cache instead of requiring the server to retrieve the data from its in-memory collections and serialize it into JSON again. This decreases bandwidth usage and improves the overall scalability of the API.

### 2.2 Workspace Deletion & Safety Logic Question
If a client needs to verify whether a specific workspace exists but wants to save bandwidth by not downloading the entire JSON body, which HTTP method should they use instead of GET? Explain your reasoning.

**Answer:** The client should use a HEAD request. The HEAD method is identical to a GET request in terms of routing, authentication, and header processing, but it does not return a response body. Instead, the server responds with the same headers and HTTP status code that would be returned by a GET request.

This allows the client to determine whether a resource exists by checking the response status code (for example, 200 OK or 404 Not Found) without downloading the full JSON payload. As a result, bandwidth usage is reduced and unnecessary data transfer is avoided, particularly when dealing with large collections of resources.

---

## PART 3: MODEL OPERATIONS & LINKING

### 3.1 Model Resource & Integrity Question
When creating a new Model via a POST request, it is considered best practice for the server to generate the unique id (e.g., using UUID.randomUUID()) rather than allowing the client to pass an id in their JSON payload. Discuss the security and data integrity reasons behind this architectural choice, contrasting Jakarta Bean Validation annotations vs manual if-else validation.

**Answer:** Allowing the client to supply a unique identifier introduces significant security and data integrity risks. From a security perspective, it may enable resource overwriting or unauthorised access, where a malicious user could intentionally guess or reuse an existing ID to modify or replace another model’s data. From a data integrity perspective, client-side ID generation can lead to collisions or inconsistent identifiers, particularly in distributed or concurrent environments where multiple requests are processed simultaneously.

By delegating ID generation to the server using mechanisms such as UUID.randomUUID(), the system ensures that identifiers are globally unique, unpredictable, and not controllable by the client. This helps maintain data integrity and prevents tampering with existing resources.

In addition, Jakarta Bean Validation provides a declarative approach to input validation using annotations such as @NotNull, @Size, and @Pattern. This is preferable to manual if-else validation because it centralises validation rules, reduces boilerplate code, and ensures consistent enforcement by the framework before business logic is executed. In contrast, manual validation can become scattered across resource classes, increasing the risk of inconsistency and human error.

### 3.2 Filtered Retrieval & Search Question
If a user attempts to search for a framework containing spaces or special characters (e.g., ?framework=Scikit Learn & Tools), how must the client modify the URL, and why is this encoding necessary?

**Answer:** The client must apply percent-encoding (URL encoding) to the query parameter values, converting the string into a safe URL format such as ?framework=Scikit%20Learn%20%26%20Tools. This is necessary because URLs are only designed to reliably support a limited set of ASCII characters.

Certain characters, such as spaces and ampersands, have special meanings within the HTTP query string structure. For example, the ampersand (&) is used to separate multiple query parameters. If these characters are not encoded, the server may incorrectly interpret them as part of the URL syntax rather than literal data, resulting in incorrect parsing of parameters or request handling errors.

---

## PART 4: DEEP NESTING WITH SUB-RESOURCES

### 4.1 Sub-Resource Locator Pattern Question
You can place annotations like @Produces(MediaType.APPLICATION_JSON) at either the class level or the individual method level. What is the benefit of class-level placement, and how does method-level overriding work?

**Answer:** Placing @Produces(MediaType.APPLICATION_JSON) at class level defines a default response media type for all methods within the resource class. This reduces duplication and improves maintainability by avoiding repeated annotations on each endpoint method.

Method-level @Produces annotations allow this default behaviour to be overridden for specific endpoints when a different response format is required, such as text/plain or another media type.

The JAX-RS runtime resolves the most specific annotation available, meaning method-level annotations take precedence over class-level annotations. This allows fine-grained control over individual endpoints while maintaining a consistent default configuration at class level.

### 4.2 Historical Data Management Note
**Implementation Logic:** Evaluation metrics are stored using a nested sub-resource under `/models/{modelId}/metrics`, implemented via a sub-resource locator in `ModelResource`. The `EvaluationMetricResource` exposes a `GET /` endpoint to retrieve the historical evaluation metrics for a specific model and a `POST /` endpoint to append new evaluation metrics to that model’s history.

When a new metric is submitted via POST, it is persisted into the model’s evaluation history. As a required side effect of this operation, the parent `MachineLearningModel` entity is also updated by setting its `latestAccuracy` field to the newly submitted accuracy score. This ensures data consistency between historical metric records and the model’s current performance state across the API.
---

## PART 5: ADVANCED ERROR HANDLING

### 5.1 Resource Conflict (409) Note
** Attempting to delete a MLWorkspace that still contains associated models triggers a custom WorkspaceNotEmptyException. This exception is handled by a dedicated JAX-RS ExceptionMapper, which converts it into an HTTP 409 Conflict response. The response includes a structured JSON error message explaining that the workspace cannot be deleted due to existing model dependencies, ensuring clear feedback to the client while maintaining API integrity.

### 5.2 Dependency Validation (422/400) Question
HTTP status codes are categorized into classes (e.g., 2xx, 4xx, 5xx). Explain fundamentally why a validation failure caused by the user providing a non-existent workspaceId must return a 4xx code rather than a 5xx code.

**Answer:** HTTP status codes are grouped into classes that indicate the origin of the response. 4xx status codes represent client errors, meaning the request was syntactically valid but contained incorrect or invalid data from the client’s perspective. In this case, the server has processed the request correctly but cannot complete it due to an invalid input, such as a non-existent workspaceId.

In contrast, 5xx status codes represent server errors, which occur when the server fails to process a valid request due to internal faults, such as unhandled exceptions or system failures.

Providing a non-existent workspaceId is therefore a client-side validation issue, as the client has referenced a resource that does not exist. The server is functioning correctly by detecting and rejecting this invalid reference, and as such, a 4xx response (such as 400 Bad Request or 422 Unprocessable Entity) is appropriate. Returning a 5xx error would incorrectly imply a server malfunction and could mislead monitoring systems and API consumers, potentially breaking error-monitoring systems and skewing operational alerts.

### 5.3 State Constraint (403) Note
**Implementation Logic:** When a MachineLearningModel is marked with the status DEPRECATED, it is no longer eligible to accept new evaluation metrics. Any attempt to submit a POST request to /models/{modelId}/metrics for such a model triggers a custom ModelDeprecatedException.

This exception is handled by a dedicated JAX-RS ExceptionMapper, which returns an HTTP 403 Forbidden response. This indicates that the request is understood by the server, but the current state of the resource prevents the operation from being performed. The response ensures that deprecated models remain immutable while providing clear feedback to the client.

### 5.4 Global Safety Net (500) Question
If an operation throws a specific custom exception (e.g., LinkedWorkspaceNotFoundException) and you also have a global ExceptionMapper<Throwable>, how does the JAX-RS runtime determine which mapper to execute?

**Answer:** The JAX-RS runtime resolves exception handling using a most specific match strategy based on the exception class hierarchy. When an exception is thrown, the runtime searches for a registered ExceptionMapper that exactly matches the exception type or the closest parent type in its inheritance chain.

If a specific mapper exists for a custom exception such as LinkedWorkspaceNotFoundException, that mapper will be selected in preference to more generic handlers. This is because it provides a direct match to the thrown exception type.

A global ExceptionMapper<Throwable> acts as a catch-all fallback handler and is only used when no more specific mapper is available. This ensures that unexpected runtime exceptions are still handled gracefully and converted into a standard HTTP 500 Internal Server Error, preventing raw stack traces from being exposed to the client.

### 5.5 API Logging Filters Question
In your filter, you interact with ContainerRequestContext and ContainerResponseContext. List two pieces of crucial HTTP metadata (e.g., headers, URIs) you can extract from these contexts that are highly valuable for debugging server issues.

**Answer:** * Request URI and HTTP method (from ContainerRequestContext), using methods such as getUriInfo().getRequestUri() and getMethod(). These are useful for identifying which endpoint is being accessed and what operation is being performed.
* Response status code and response headers (from ContainerResponseContext), using methods such as getStatus() and getHeaders(). These help determine the outcome of the request and provide additional diagnostic information for debugging server behaviour.
