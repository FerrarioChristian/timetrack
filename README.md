# TimeTrack - Activity Time Tracker

A modern web application designed for personal time management and productivity tracking. TimeTrack allows users to categorize their daily tasks, log time sessions, track productivity goals, and visualize data through dynamic dashboard statistics.

## Key Features

- **Modern Shadcn-Style UI:** Beautiful, responsive dark-mode interface built with Tailwind CSS and Lucide Icons for an elegant user experience.
- **Real-Time Tracking & Pomodoro:** Start, pause, and stop tracking sessions for any activity. Includes a built-in Pomodoro mode for structured 25-minute work intervals with automatic logging.
- **Goal Management:** Set specific time goals for your activities and categories to stay on track and measure progress.
- **Manual Session Management:** Fully edit, add, or delete past time entries to ensure your time logs are always perfectly accurate.
- **Interactive Dashboard:** Dynamic insights and visual charts (powered by Chart.js) displaying your productivity breakdown, active timers, and total time spent.
- **Secure Authentication:** Complete user registration and login flows powered by Spring Security, ensuring complete data isolation between users.

## Technology Stack

**Backend**
- **Java 23 & Spring Boot 3:** The core framework driving the application.
- **Spring Security:** Handles authentication, authorization, and route protection.
- **Spring Data JPA & Hibernate:** ORM layer for seamless database interactions.
- **PostgreSQL:** Reliable relational database for persistent data storage.

**Frontend**
- **Thymeleaf:** Server-side template engine for rendering dynamic HTML views.
- **Tailwind CSS & Chart.js:** Replaces legacy CSS frameworks for a clean, modern, and mobile-friendly dark UI with data visualizations.

## Technical Highlights

- **Data Isolation & Security:** Implementation of `@PreAuthorize` annotations at the service layer to strictly enforce data ownership, preventing unauthorized access to other users' activities and sessions. CSRF protection is active on all forms and Javascript requests.
- **Time Representation:** Utilizing robust `java.time.Duration` and `java.time.LocalDateTime` objects throughout the backend for precise time manipulation, coupled with custom Formatters to seamlessly render durations as readable strings.
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
