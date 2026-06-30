# MLOps Pipeline Management API Report

## 1. Overview of API Design
This project implements a robust RESTful API built using the Quarkus framework and Java to manage machine learning operations (MLOps) pipelines. The API provides a clean, decoupled architecture to handle enterprise-level Workspaces, deployable machine learning Models, and automated processing Pipelines.

The resource management model enforces strict data isolation and logical validation boundaries:
* Workspace: Represents the top-level logical boundary or tenant environment for an analytics team.
* Model: Represents individual machine learning models (tracking versions and frameworks) linked to a specific Workspace.
* Pipeline: Controls the automated execution paths for data preparation and deployment tasks.

---

## 2. Step-by-Step Instructions

### Prerequisites
* **Java Development Kit (JDK) 17** or higher
* **Apache Maven 3.8.x** or higher
* **Mac Terminal Application** or the integrated terminal environment within IntelliJ IDEA

### Step 1: Build the Project
To clean previous build artifacts, compile the source code, run unit tests, and package the application into an executable JAR file, run the following command in your Mac Terminal application from the project root directory:
```bash
mvn clean package

Step 2: Launch the Server
Once the build is complete, use the following command to spin up the Quarkus server in development mode. This activates live-reloading features and hosts the server locally on port 8080:

Bash
mvn quarkus:dev
The base API entry point is accessible at: http://localhost:8080

```
---

### 3. Sample cURL Commands
The following five cURL commands demonstrate successful HTTP interactions across different parts of the deployed API resources.

1. Create a New Workspace (POST)
Bash
curl -X POST http://localhost:8080/workspaces \
  -H "Content-Type: application/json" \
  -d '{"name": "Production Analytics", "owner": "M-Kaur-Singh"}'

2. Retrieve All Workspaces (GET)
Bash
curl -X GET http://localhost:8080/workspaces


3. Add a Model to a Workspace (POST)
Bash
curl -X POST http://localhost:8080/models \
  -H "Content-Type: application/json" \
  -d '{"name": "Churn-Prediction-Model", "version": "v1.0.0", "workspaceId": 1}'
  
  
4. Fetch Details of a Specific Model (GET)
Bash
curl -X GET http://localhost:8080/models/1

5. Trigger a Pipeline Execution (POST)
Bash
curl -X POST http://localhost:8080/pipelines/1/execute \
  -H "Content-Type: application/json" \
  -d '{"triggeredBy": "Automation-Agent"}'

---

## 4. Conceptual Report: Coursework Answers

## PART 1: SETUP & DISCOVERY

### 1.1 Architecture & Config Question
When returning a Java object from a method, it is automatically serialized into JSON. Explain the role of a MessageBodyWriter or a JSON provider (like Jackson) in this conversion process.

**Answer:** A MessageBodyWriter is a core architectural interface within the JAX-RS framework. It manages the marshaling of native Java objects (POJOs) into a specific raw stream format for HTTP responses. When an API endpoint method returns a plain Java object and is decorated with the @Produces(MediaType.APPLICATION_JSON) annotation, the JAX-RS runtime looks for an active provider. Jackson serves as this concrete JSON provider. It handles low-level processing by using reflection to inspect class properties. It invokes the appropriate getter methods and maps those key-value states dynamically into a valid JSON text string written directly into the HTTP outbound response body.

### 1.2 Discovery Endpoint Question
REST architecture dictates that APIs should be strictly 'stateless'. Define what statelessness means in this context and explain why it makes cloud APIs easier to scale horizontally across multiple servers.

**Answer:** In RESTful system design, statelessness means that the server application is completely prohibited from preserving or caching any client session state across transactional boundaries. Every incoming HTTP request must arrive as an isolated, completely self-contained payload. It must embed all necessary metadata, credentials, and query targets required to complete the operation.

This requirement directly simplifies horizontal scaling in cloud infrastructure. Because individual instances do not store sticky session data in memory, a load balancer can route consecutive requests from the same user to entirely different physical servers. This route changes without causing state desynchronization errors and removes the need for complex, resource-heavy multi-node session replication systems.

---

## PART 2: WORKSPACE MANAGEMENT

### 2.1 Workspace Resource Implementation Question
Discuss how implementing HTTP Cache-Control headers on the GET workspaces endpoint could improve performance for the client and reduce unnecessary processing load on the server.

**Answer:** Appending explicit Cache-Control directive headers (such as max-age=30) tells the client's browser or upstream proxy caches that they can store and reuse a snapshot of the workspace collection locally. For the client, this dramatically increases responsiveness. Cached datasets are retrieved instantly from local storage without paying a network latency penalty. For the server, it lowers resource consumption because it intercepts redundant requests before they hit the resource layer. This stops unnecessary database execution loops and JSON parsing overhead for static data.

### 2.2 Workspace Deletion & Safety Logic Question
If a client needs to verify whether a specific workspace exists but wants to save bandwidth by not downloading the entire JSON body, which HTTP method should they use instead of GET? Explain your reasoning.

**Answer:** The client should issue a HEAD request. The HEAD method mirrors the exact routing logic, access controls, and header generation of a standard GET request, but it explicitly tells the server to drop the response payload entirely. The server returns only the initial HTTP status line and header metadata. By checking for a 200 OK or 404 Not Found response code, the client can confidently verify if a resource exists without wasting bandwidth transferring large collections of JSON text.

---

## PART 3: MODEL OPERATIONS & LINKING

### 3.1 Model Resource & Integrity Question
When creating a new Model via a POST request, it is considered best practice for the server to generate the unique id (e.g., using UUID.randomUUID()) rather than allowing the client to pass an id in their JSON payload. Discuss the security and data integrity reasons behind this architectural choice, contrasting Jakarta Bean Validation annotations vs manual if-else validation.

**Answer:** Delegating unique identifier provisioning to the client presents significant structural and security risks. From a security perspective, it exposes the API to resource hijacking attacks, where a malicious user could intentionally guess and supply an ID belonging to another project to overwrite data records. From an integrity perspective, client-side generation can trigger high collision rates in distributed systems when multiple users simultaneously attempt to reserve sequential index numbers. Isolating ID generation to the server side via cryptographically secure tools like random UUIDs ensures strict uniqueness, preserves backend data models, and stops data corruption or tampering.

Furthermore, leveraging declarative Jakarta Bean Validation constraints (such as `@NotNull` or `@Size`) provides a superior framework-level security guarantee compared to manual if-else validation code blocks. Manual if-else checks scatter defensive logic across resource classes, making the application error-prone and hard to maintain. Jakarta Bean Validation cleanly decouples validation constraints from business logic, ensuring automated, uniform interceptor enforcement at the framework boundary before runtime data persistence operations run.

### 3.2 Filtered Retrieval & Search Question
If a user attempts to search for a framework containing spaces or special characters (e.g., ?framework=Scikit Learn & Tools), how must the client modify the URL, and why is this encoding necessary?

**Answer:** The client must apply percent-encoding (URL encoding) to the query values, transforming the argument string into ?framework=Scikit%20Learn%20%26%20Tools. This parsing is necessary because native URLs only support safe ASCII strings. Characters like spaces and ampersands carry structural meanings inside the HTTP protocol specs. If left unencoded, an ampersand (&) would be misinterpreted by the server's HTTP engine as a parameter key delimiter rather than literal text data. This breaks the query parsing logic and causes execution errors.

---

## PART 4: DEEP NESTING WITH SUB-RESOURCES

### 4.1 Sub-Resource Locator Pattern Question
You can place annotations like @Produces(MediaType.APPLICATION_JSON) at either the class level or the individual method level. What is the benefit of class-level placement, and how does method-level overriding work?

**Answer:** Declaring @Produces(MediaType.APPLICATION_JSON) at the class level establishes a clean, shared fallback rule for the entire resource controller, reducing boilerplate code across all inner methods. Method-level overriding allows developers to break away from this global rule for specific endpoints that need to output alternate media variants, like a data download endpoint marked with @Produces(MediaType.TEXT_PLAIN). The JAX-RS runtime evaluates these targets by prioritizing the narrowest, most specific path declaration, allowing the method annotation to override the broader class fallback smoothly. This pattern is necessary for managing sub-resource locators that resolve nested components dynamically.

### 4.2 Historical Data Management Note
**Implementation Logic:** To manage performance metrics and accuracy over time, historical data endpoints are exposed under deep nesting routes. Successful POST operations targeting performance tracking sub-resources dynamically fire automated callbacks to adjust and update the parent machine learning model record's data states seamlessly.

---

## PART 5: ADVANCED ERROR HANDLING

### 5.1 Resource Conflict (409) Note
**Implementation Logic:** Deleting a Workspace containing active elements triggers a custom `WorkspaceNotEmptyException`. A dedicated JAX-RS ExceptionMapper catches this exception to cleanly pass back an HTTP 409 Conflict code bundled with a structured error explanation payload.

### 5.2 Dependency Validation (422/400) Question
HTTP status codes are categorized into classes (e.g., 2xx, 4xx, 5xx). Explain fundamentally why a validation failure caused by the user providing a non-existent workspaceId must return a 4xx code rather than a 5xx code.

**Answer:** The HTTP specification states that status code groupings reflect where the error originated. 4xx responses are reserved for Client Errors, meaning the backend server processed the inbound stream safely, but the client sent an unprocessable payload, invalid arguments, or a bad structural reference. Conversely, 5xx codes specify Server Errors, meaning the incoming request was completely valid, but the internal server code crashed or suffered an unhandled runtime exception.

Providing a missing or incorrect workspaceId is a user-driven input error. The API server is executing perfectly by catching this mistake, meaning a 4xx error (such as 400 Bad Request or 422 Unprocessable Entity via `LinkedWorkspaceNotFoundException`) must be passed back. Throwing a 5xx code here would be highly misleading, as it falsely indicates a server bug and breaks error-monitoring systems.

### 5.3 State Constraint (403) Note
**Implementation Logic:** Modifying or posting tasks against deprecated workflows triggers a specialized `ModelDeprecatedException`. An architectural exception handler isolates this event and seamlessly returns an HTTP 403 Forbidden error response to block unlawful changes.

### 5.4 Global Safety Net (500) Question
If an operation throws a specific custom exception (e.g., LinkedWorkspaceNotFoundException) and you also have a global ExceptionMapper<Throwable>, how does the JAX-RS runtime determine which mapper to execute?

**Answer:** The JAX-RS architecture relies on a strict inheritance proximity algorithm (closest-match mapping) to determine exactly which exception interceptor to execute. When a handler is triggered, the runtime scans the thrown object's type tree against all registered ExceptionMapper parameters. It will always prioritize the mapper that sits closest to the exception class type.

Because a specialized exception has a direct, exact relationship with its concrete mapper, its structural proximity distance is zero. The root Throwable interface is the absolute top-level parent class, placing it at the furthest possible inheritance distance. Consequently, JAX-RS chooses the specific mapper first and drops back to the catch-all Throwable safety net (returning a generic HTTP 500 Internal Server Error) only if an unhandled unexpected runtime failure takes place.

### 5.5 API Logging Filters Question
In your filter, you interact with ContainerRequestContext and ContainerResponseContext. List two pieces of crucial HTTP metadata (e.g., headers, URIs) you can extract from these contexts that are highly valuable for debugging server issues.

**Answer:** * First item: The Request Inbound URI and HTTP Verb (ContainerRequestContext.getUriInfo().getRequestUri() / .getMethod()). Custom filters implementing ContainerRequestFilter extract these metadata elements to explicitly trace target entry routes and user actions executed against system resources.
* Second item: The Response Status Code and Content Length (ContainerResponseContext.getStatus() / .getLength()). Interceptors implementing ContainerResponseFilter capture these states to monitor outbound data traffic. Logging these elements together within centralized console loops builds a flawless tracking log showing HTTP methods, request URIs, and status results for all incoming operations.