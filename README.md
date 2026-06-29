# MLOps Pipeline API

## 1. Overview of API Design
This project implements a robust RESTful API built using Quarkus and Java to manage machine learning operations (MLOps). The API provides a structured architectural design to manage enterprise-level **Workspaces**, deployable machine learning **Models**, and automated processing **Pipelines**.

The system relies on clear data boundaries:
* **Workspace**: Represents an isolated environment or project team boundary.
* **Model**: Represents individual machine learning models (e.g., versioning, algorithms) housed inside a Workspace.
* **Pipeline**: Automates tasks linked to data transformation and model training workflows.

---

## 2. Step-by-Step Instructions

### Prerequisites
* Java Development Kit (JDK) 17 or higher
* Apache Maven 3.8.x or higher

### Step 1: Build the Project
To compile the source code, run the unit tests, and package the application into a runnable JAR file, execute the following command in your terminal root directory:
```bash
mvn clean package