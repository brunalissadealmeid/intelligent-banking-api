# Intelligent Banking API 

This project is a smart banking API built with Java and Spring Boot, simulating core banking operations with integrated machine learning features such as credit risk analysis and fraud detection.

---

##  Features

-  Account creation and user authentication
-  Deposit, withdrawal, and balance check
-  Transaction history and reporting
-  Credit risk evaluation using ML (Python model)
-  Fraud detection using anomaly detection (Isolation Forest)
-  Product recommendation engine (K-Means clustering)
-  RESTful API architecture documented with Swagger

---

##  AI/ML Integration

###  Credit Risk Assessment
A supervised machine learning model (Logistic Regression) trained on synthetic credit data to evaluate loan eligibility based on income, age, balance, etc.

> Endpoint: `POST /credit-analysis`

###  Fraud Detection
Anomaly detection using Isolation Forest to identify unusual transaction patterns.

> Endpoint: Automatic detection during transaction registration

###  Smart Product Recommendation
Cluster-based recommendation (K-Means) based on user profile and behavior.

> Endpoint: `GET /recommendations/{userId}`

---

## ⚙ Tech Stack

- Java 17
- Spring Boot
- JPA / Hibernate
- PostgreSQL
- Swagger / OpenAPI
- Python (for ML models)
- Docker (optional)
- Maven

---

##  Project Structure
 intelligent-banking-api
┣  src
┃ ┣  main
┃ ┃ ┣  java
┃ ┃ ┣  resources
┣  models (Python)
┣  README.md
┣  pom.xml


