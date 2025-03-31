# Classroom Control Project

This project is a microservices-based application developed with **Spring Boot**, utilizing **Kafka**, **MongoDB**, and deployed on **Kubernetes**. Its primary goal is to efficiently manage access to virtual classrooms.

## General Description
The application allows:
- Creating, deleting, listing, and pausing classrooms via an API.
- Generating a dynamic queue for each created classroom, allowing users to join the queue automatically if the classroom is full.
- Scheduling the automatic eviction of the classroom after a set period (configurable via the `room.duration` variable).

## Key Technologies
- **Spring Boot** - Backend development with a modular and scalable approach.
- **Kafka** - Efficient event handling and concurrency management using partitions.
- **MongoDB** - Database for persisting classroom and user information.
- **Kubernetes** - Orchestration and deployment of microservices in a distributed environment.
- **Testcontainers** - Integration testing with real MongoDB and Kafka instances.
- **Prometheus and Grafana** - Monitoring system metrics and custom events.

## Key Technical Aspects

### 1. Dynamic Kafka Topics and Listeners Management
- When a classroom is created, the system automatically generates:
    - A **topic** to manage the queue for that classroom.
    - A **listener** associated with that topic.
- If the classroom becomes full or is paused, the listener temporarily stops consuming messages. If the listener was processing a message when paused, the process is rolled back, and the offset is not committed, ensuring the message is processed once the classroom resumes.
- When a classroom is deleted:
    - The **topic** and its **listener** are also deleted.

### 2. Listener State Persistence
- If a pod restarts, listeners are restored to the same state the classroom was in (paused or active).

### 3. Concurrency Management
- To enable concurrent user processing, classroom topics are created with **multiple partitions**. This improves performance but means message order is not guaranteed.

### 4. Stress Testing
- Added a dedicated endpoint to add 1000 users to a queue, evaluating the application's performance and scalability.

### 5. Automated Testing
- Unit and integration tests were implemented using **Testcontainers**, simulating real MongoDB and Kafka instances.

### 6. Monitoring and Observability
- Integrated with **Prometheus** and **Grafana** to:
    - Visualize performance metrics, including a custom metric: `kafka.message.latency`.
    - Monitor latency times, topic activity, and message consumption.
- Added **AKHQ** as a client to manage Kafka topics, consumer groups, and other critical components.

## Project Execution
1. Deploy the environment with Kubernetes using the provided deployment files.
3. Access the monitoring tools:
    - **Prometheus** on port `9090`
    - **Grafana** on port `3000`
    - **AKHQ** for Kafka management

## Contact
If you would like to know more details about the project or have technical questions, feel free to contact me. This project focuses on managing topics, listeners, partitions, and offsets. Other aspects such as testing, monitoring, exception handling, and security were not covered in depth.

