# Person Detector (Microservices Application)

Person Detector is a microservices-based application designed for detecting people in images using OpenCV. It consists
of two independent services:

1. **REST API Service** - Responsible for handling client requests and connecting to a database for storing and
   retrieving analysis results.
2. **OpenCV Service** - Performs image processing and analysis to detect people in submitted images.

These services communicate using a **message queue**. The REST API service uploads images and notifies the OpenCV
service via the queue. Once the OpenCV service processes the image, it sends the detection results back via **HTTP
communication** to the REST API service.

## Specification

- **Microservices architecture** with two independent services
- **Spring Boot** for the REST API service
- **OpenCV** for image processing and analysis
- **Database integration** for storing image analysis results
- **Message Queue (RabbitMQ)** for asynchronous communication between services
- **RESTful API** for communication between services
- **Docker containerization** for easy deployment and scalability
- **Maven** for building the services

## Tech Stack

### Core:

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![OpenCV](https://img.shields.io/badge/OpenCV-5C3EE8?style=for-the-badge&logo=opencv&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

### Testing:

![JUnit5](https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78A641?style=for-the-badge)
![Testcontainers](https://img.shields.io/badge/Testcontainers-9B489A?style=for-the-badge)

## Installation and Run

### Requirements:

- [Docker](https://www.docker.com/products/docker-desktop/) for running containers
- [Java 21](https://jdk.java.net/21/) for running the backend service
- [Maven](https://maven.apache.org/) for building the services

### To run the application:

Replace application.properties.template files with application properties. These are files defined for the startup
script.
Downoload [yolov4.weights](https://drive.google.com/file/d/1J0cJhcSZjzy24nNwllUpDokpp6iPSKz7/view?usp=sharing) and put it into photo-analysis resources folder.

Run the following script to start the microservices:

```
./start_microservices.ps1
```

To stop the services, use:

```
./stop_microservices.ps1
```

Alternatively, you can start the services manually via your IDE.

A **Postman collection** is available in the project for testing API requests.

## Rest-API Endpoints

| ENDPOINT                                   | METHOD | REQUEST                             | RESPONSE | FUNCTION                                     |
|--------------------------------------------|--------|-------------------------------------|----------|----------------------------------------------|
| /api/v1/tasks                              | POST   | FORM-DATA (file)                    | JSON     | Upload an image for analysis                 |
| /api/v1/tasks/url                          | POST   | FORM-DATA (url)                     | JSON     | Submit an image for analysis via URL         |
| /api/v1/tasks                              | GET    | NONE                                | JSON     | Retrieve all submitted tasks                 |
| /api/v1/tasks/{id}                         | GET    | PATH VARIABLE (id)                  | JSON     | Retrieve details of a specific task          |
| /api/v1/tasks/{id}/image                   | GET    | PATH VARIABLE (id)                  | JSON     | Retrieve the processed image for a task      |
| /api/v1/tasks/count                        | GET    | NONE                                | JSON     | Retrieve count of tasks by status            |
| /api/v1/tasks/{id}/status                  | PUT    | BODY-JSON (status)                  | NONE     | Update the status of a task                  |
| /api/v1/tasks/{id}/status/detected-persons | PUT    | BODY-JSON (status, detectedPersons) | NONE     | Update the status and detected persons count |

> AUTHOR: ZIELEEKSW  
> GITHUB: [Person Detector Repository](https://github.com/zieleeksw/person_detector)

