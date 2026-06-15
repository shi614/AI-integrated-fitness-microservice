🏋️ AI-Integrated Fitness Microservice

An AI-powered Fitness Tracking Platform built using Spring Boot Microservices, React, Apache Kafka, Eureka Service Discovery, API Gateway, JWT Authentication, and Gemini AI.

🚀 Overview

This project helps users track their fitness activities and receive AI-generated fitness recommendations based on their workout history.

The application follows a Microservices Architecture, making it scalable, maintainable, and production-ready.

---
✨ Features
 👤 User Management

* User Registration
* User Authentication
* JWT-based Security
* User Profile Management

🏃 Activity Tracking

* Add Fitness Activities
* Track Workout Duration
* Monitor Calories Burned
* View Activity History
 🤖 AI Recommendations
* Personalized Fitness Suggestions
* Gemini AI Integration
* Activity-Based Recommendations
  ⚡ Microservices Architecture
* API Gateway
* Service Discovery (Eureka)
* Event-Driven Communication using Kafka
* Independent Service Deployment
🏗️ System Architecture

Frontend (React)
↓
API Gateway
↓
├── User Service
├── Activity Service
└── AI Service
↓
Apache Kafka
↓
Gemini AI

🛠️ Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Cloud Gateway
* Spring WebFlux
* Eureka Server
* Apache Kafka
* JWT Authentication
* Maven

### Frontend

* React.js
* Axios
* Tailwind CSS

### AI Integration

* Google Gemini API

### Database

* MongoDB

---

## 📂 Project Structure

AI-Integrated-Fitness-Microservice

├── fitness-microservice-backend

│ ├── api-gateway

│ ├── user-service

│ ├── activity-service

│ ├── ai-service

│ └── service-registry

│

└── fitness-microservice-frontend

```
└── fitness-frontend
```

---

## 🔄 Workflow

1. User registers and logs in.
2. JWT token is generated.
3. User adds fitness activities.
4. Activity Service stores activity data.
5. Activity events are published to Kafka.
6. AI Service consumes Kafka messages.
7. Gemini AI generates personalized recommendations.
8. Recommendations are displayed to the user.

---

📸 Screenshots
<img width="1920" height="1200" alt="Screenshot (9)" src="https://github.com/user-attachments/assets/636fd15b-5743-4a50-addd-b42821d02475" />
<img width="1920" height="1200" alt="Screenshot (6)" src="https://github.com/user-attachments/assets/75cff835-1809-4830-8df7-4e46115b2635" />



## 🔮 Future Enhancements

* Nutrition Recommendations
* Goal Tracking
* Workout Analytics Dashboard
* Wearable Device Integration
* Email Notifications
* AI-based Health Insights

---

## 👨‍💻 Author

Shivi Chauhan

Passionate about Backend Development, Microservices, AI Integration, and Scalable Systems.

---

## ⭐ Support

If you found this project useful, please consider giving it a star.
