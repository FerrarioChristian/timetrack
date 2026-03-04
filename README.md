# TimeTrack - Activity Time Tracker

Web application designed for personal time management and productivity tracking. TimeTrack allows users to categorize their daily tasks, log time sessions, and visualize their productivity through dynamic statistics. 

![record](/docs/record.gif)

## Key Features

- **Secure Authentication:** Complete user registration and login flows powered by Spring Security, ensuring complete data isolation between users.
- **Activity & Category Management:** Users can create custom categories, define tasks, and set specific time goals (`GOAL`) or limits (`MAX_TIME`) for their activities.
- **Real-Time Tracking:** Start, pause, and stop tracking sessions for any activity. The application automatically calculates and saves accurate session durations.
- **Dynamic Statistics:** Generates on-demand insights including average time per session, daily averages, and total time spent across different timeframes (e.g., last week, last month).

## Technology Stack

**Backend**
- **Java 23 & Spring Boot 3:** The core framework driving the application.
- **Spring Security:** Handles authentication, authorization, and route protection.
- **Spring Data JPA & Hibernate:** ORM layer for seamless database interactions.
- **PostgreSQL:** Reliable relational database for persistent data storage.

**Frontend**
- **Thymeleaf:** Server-side template engine for rendering dynamic HTML views.
- **Bootstrap:** Ensures a clean, responsive, and mobile-friendly UI.

## Technical Highlights

- **Data Isolation & Security:** Implementation of `@PreAuthorize` annotations at the service layer to strictly enforce data ownership, preventing unauthorized access to other users' activities and sessions.
- **Time Representation:** Utilizing robust `java.time.Duration` objects throughout the backend for precise time manipulation, coupled with custom Formatters and Converters to seamlessly render durations as readable `hh:mm:ss` strings on the frontend.
- **Clean Architecture:** Adheres strictly to the **Model-View-Controller (MVC)** design pattern, maintaining a clean separation of concerns among entities, business logic (Services), and request handling (Controllers).

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 23 or higher
- PostgreSQL Database installed and running
- Maven (Optional, the project includes the Maven Wrapper `mvnw`)

### Setup & Run

1. **Clone the repository**
   ```bash
   git clone git@gitlab.com:chri.fer.emi/timetrack.git
   cd timetrack
   ```

2. **Configure the Database**
   Create a new PostgreSQL database for the project.

3. **Environment Variables**
   Create a `.env` file in the root directory and provide your database credentials:
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/<your-database-name>
   DB_USER=<your-username>
   DB_PASS=<your-password>
   ```

4. **Launch the Application**
   Run the project using the included Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the App**
   Open your browser and navigate to `http://localhost:8080`
