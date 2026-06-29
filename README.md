# MLOps Pipeline Management API Report

## Part 1: Service Architecture & Setup Answers

### Question 1
When returning a Java object from a method, it is automatically serialised into JSON. Explain the role of a MessageBodyWriter or a JSON provider (like Jackson) in this conversion process.

**Answer:** A MessageBodyWriter is a key interface in the JAX-RS framework responsible for converting native Java objects (like your POJOs) into a specific output format sent over the HTTP response stream. When your method returns an object and has a @Produces(MediaType.APPLICATION_JSON) annotation, a JSON provider like Jackson kicks in. Jackson acts as the concrete implementation of the MessageBodyWriter. It uses Java Reflection to inspect your class's properties, reads the private fields using your getters, and dynamically formats that data into a clean, structured JSON string that web browsers and client applications can read.

---

### Question 2
REST architecture dictates that APIs should be strictly 'stateless'. Define what statelessness means in this context and explain why it makes cloud APIs easier to scale horizontally across multiple servers.

**Answer:** In REST architecture, statelessness means that the server does not store or remember any data about previous client interactions or active user sessions. Every single inbound HTTP request must be completely self-contained, carrying all the authentication tokens, parameters, and identifiers necessary to execute that task.

This makes horizontal scaling in the cloud significantly easier because incoming traffic can be spread out via a load balancer across dozens of different servers. Since no server holds an "in-memory session," any server instance can immediately process any incoming request without needing to sync user session states or memory pools with other machines across the network.



## Part 2: Workspace Management Answers

### Question 1
Discuss how implementing HTTP Cache-Control headers on the GET workspaces endpoint could improve performance for the client and reduce unnecessary processing load on the server.

**Answer:** Implementing `Cache-Control` headers (such as `max-age=30`) allows the client or intermediary proxy servers to store local copies of the workspace list. This improves client performance by rendering data instantly from memory without waiting for a network trip. For the server, it prevents redundant processing cycles spent fetching data or serialising data collections into JSON for identical requests, significantly optimizing server resource utilisation.

### Question 2
If a client needs to verify whether a specific workspace exists but wants to save bandwidth by not downloading the entire JSON body, which HTTP method should they use instead of GET? Explain your reasoning.

**Answer:** The client should use the `HEAD` method. The `HEAD` method behaves identically to a `GET` request, but the server skips rendering the actual response body and only returns the status line and header metadata. This allows the client to inspect the HTTP status code (e.g., `200 OK` vs `404 Not Found`) to verify existence without transferring any heavy JSON payload data across the network.


## Part 3: Model Operations & Linking Answers

### Question 1
When creating a new Model via a POST request, it is considered best practice for the server to generate the unique id (e.g., using UUID.randomUUID()) rather than allowing the client to pass an id in their JSON payload. Discuss the security and data integrity reasons behind this architectural choice.

**Answer:** Allowing the client to provision resource identifiers presents major security risks, such as ID hijacking, where a malicious actor intentionally overwrites an existing record by providing an identical ID. It can also cause accidental data integrity collisions in concurrent environments. When the server retains exclusive control over ID generation (using secure systems like random UUIDs), it guarantees total uniqueness, protects existing data structures from collision errors, and prevents malicious clients from guessing database sequence lengths.

### Question 2
If a user attempts to search for a framework containing spaces or special characters (e.g., ?framework=Scikit Learn & Tools), how must the client modify the URL, and why is this encoding necessary?

**Answer:** The client must perform URL encoding (percent-encoding) on the query parameters, translating the string into `?framework=Scikit%20Learn%20%26%20Tools`. This modification is mandatory because characters like spaces, ampersands (`&`), and question marks carry strict functional control meanings within the HTTP standard structure. An unencoded ampersand would mistake the query string as a delimiter for an entirely new parameter name, corrupting the inbound request payload parsing on the server.


## Part 4: Deep Nesting with Sub-Resources Answers

### Question 1
You can place annotations like @Produces(MediaType.APPLICATION_JSON) at either the class level or the individual method level. What is the benefit of class-level placement, and how does method-level overriding work?

**Answer:** Class-level annotation eliminates repetitive boilerplate code by establishing a universal fallback content-type configuration for all methods inside that resource class. Method-level overriding allows developers to specify an alternate media standard on a single specific endpoint (for example, applying `@Produces(MediaType.TEXT_PLAIN)` to an export method). JAX-RS prioritises the narrower, method-level rule, completely bypassing the broader class fallback configuration for that single transaction.



## Part 5: Dependency Validation & State Constraint Answers

### Question
HTTP status codes are categorised into classes (e.g., 2xx, 4xx, 5xx). Explain fundamentally why a validation failure caused by the user providing a non-existent workspaceId must return a 4xx code rather than a 5xx code.

**Answer:** Under the HTTP protocol design rules, `4xx` status codes represent client-side errors, meaning the server successfully received the request but cannot process it because the client sent bad parameters or invalid business references. On the other hand, `5xx` codes indicate server-side failures, meaning the server crashed, encountered an unhandled code bug, or cannot complete a valid request. Since a non-existent `workspaceId` is entirely caused by an incorrect client data submission, it must return a `4xx` code (like `422` or `400`) to clarify that the client needs to correct their payload before retrying, protecting the API's observability metrics.




## Part 5: Global Safety Net & Logging Filter Answers

### Question 1
If an operation throws a specific custom exception (e.g., LinkedWorkspaceNotFoundException) and you also have a global ExceptionMapper<Throwable>, how does the JAX-RS runtime determine which mapper to execute?

**Answer:** The JAX-RS runtime evaluates class inheritance proximity to find the single most specific exception match available. It calculates the inheritance distance between the thrown exception and the type declared by the exception mapper. Because `LinkedWorkspaceNotFoundException` is a direct structural match for `ExceptionMapper<LinkedWorkspaceNotFoundException>`, its inheritance path distance is zero, whereas the root fallback `Throwable` provider has a much wider distance. The runtime prioritises the closest type-specific mapper and only defaults to the `Throwable` safety net if no closer match exists.

### Question 2
In your filter, you interact with ContainerRequestContext and ContainerResponseContext. List two pieces of crucial HTTP metadata (e.g., headers, URIs) you can extract from these contexts that are highly valuable for debugging server issues.

**Answer:** 1. **The Request Headers (`ContainerRequestContext.getHeaders()`):** This allows engineers to extract vital identity or verification attributes such as authorization tokens, authentication states, and user-agent types to trace precisely who triggered an operation.
2. **The Response Execution Time or Content Length (`ContainerResponseContext.getLength()`):** This helps inspect out-of-bounds entity sizes or missing resource headers to quickly troubleshoot truncation bugs or payload overhead drops during live server transactions.
