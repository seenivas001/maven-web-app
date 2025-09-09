# 🏔️ Spring Boot DevOps Pipeline – Ski Station Management

This project is a complete backend application developed with Spring Boot to manage a ski station. It is fully integrated with a CI/CD pipeline using Jenkins and includes monitoring with Prometheus and Grafana.

## 📌 Features

- Manage skiers, subscriptions, courses, instructors, registrations
- RESTful APIs documented with Swagger / OpenAPI
- Connects to MySQL/H2 database
- Unit testing with JUnit and Mockito
- Quality analysis with SonarQube

## 🚀 CI/CD Pipeline with Jenkins

The project is integrated with a complete Jenkins pipeline:

- 🔁 GitHub clone
- 🔧 Maven build & test
- 🧪 SonarQube analysis
- 📦 Nexus deployment (Snapshots & Releases)
- 🐳 Docker image build and push to Docker Hub
- 📦 Docker Compose (backend + MySQL)
- 📬 Email notifications at the end of pipeline (success or failure)

### 📧 Email Notification

The pipeline sends an email to the developer team after execution — whether it's successful or failed — to notify the result and logs.

## 📊 Monitoring

Prometheus scrapes the application's metrics exposed via `/actuator/prometheus`.  
Grafana dashboards are created to visualize:

- JVM memory usage
- HTTP request latency and count
- CPU usage
- Application status (`up` metric)

## ⚙️ Technologies Used

| Tool        | Purpose                       |
|-------------|-------------------------------|
| Spring Boot | Java backend framework        |
| Maven       | Build tool                    |
| Jenkins     | CI/CD automation              |
| SonarQube   | Code quality analysis         |
| Nexus       | Artifact repository (Maven)   |
| Docker      | Containerization              |
| Prometheus  | Metrics collection            |
| Grafana     | Visualization & monitoring    |


