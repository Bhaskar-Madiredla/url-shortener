# URL Shortener Microservice

A high-performance, containerized URL shortening service built with a focus on scalability, low latency, and efficient resource utilization.

## 🚀 Architecture
The project utilizes a microservice architecture orchestrated via Docker Compose:
* **Backend:** Java 17, Spring Boot, Spring Data JPA.
* **Frontend:** React (Containerized).
* **Database:** PostgreSQL 15 (Persistence).
* **Cache:** Redis 7 (Latency optimization).
* **Performance Monitoring:** Custom Metrics API for real-time telemetry.

## 📊 Performance Benchmarks
Tested under concurrent load using `k6` to validate architectural improvements:

| Metric | Result |
| :--- | :--- |
| **Average Latency (End-to-End)** | **2.02 ms** |
| **Sustained Throughput** | **~0.5 requests/second (1 VU)** |
| **Cache Hit Optimization** | Implemented Redis caching to offload DB traffic |

*Benchmarked using `docker network` load simulations. Performance data captured via internal `/api/metrics` API.*

## 🛠 Features
* **Containerized Deployment:** Fully reproducible environment using Docker Compose.
* **Optimized Data Access:** Implemented connection pooling via HikariCP and Redis-first retrieval strategies.
* **Real-Time Telemetry:** Custom `/api/metrics` endpoint providing insights into system uptime, request latency, and throughput.

## 📜 License
This project is licensed under the MIT License. 
---
Copyright (c) 2026 Bhaskar Madiredla
