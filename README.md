# 🏥 MediBook — Smart Appointment Booking System

MediBook is a full-stack, modern doctor appointment booking web application built with **Spring Boot**, **React (Vite)**, **Spring Security**, **JWT Authentication**, and **H2/MySQL Database**.

---

## 🌟 Key Features

### 👤 Patient Role
- **Browse & Search Doctors**: Filter by department, experience, rating, or fee.
- **Interactive Booking**: View real-time available date strips and time slots.
- **Double-Booking Protection**: Prevents multiple bookings on the same doctor time slot.
- **Patient Dashboard**: Overview of upcoming, completed, and cancelled appointments with one-click cancellation.
- **Instant Booking Confirmation**: Visual confirmation receipt page.

### 👨‍⚕️ Doctor / Admin Role
- **SaaS Doctor Dashboard**: Real-time stats (Today's count, Pending requests, Completed cases, Total patients).
- **Appointment Management**: One-click Accept/Reject pending patient requests.
- **Schedule Availability Manager**: Add new custom dates & time slots or release existing slots.

---

## 🛠️ Technology Stack

- **Frontend**: React 18, Vite, React Router v6, Axios, Lucide React Icons, Modern Vanilla CSS Design System.
- **Backend**: Java 17, Spring Boot 3.2, Spring Data JPA, Spring Security, JWT (JJWT).
- **Database**: H2 In-Memory (configured for zero-setup execution) & MySQL dialect ready.

---

## 🔑 Demo Accounts (For Viva & Presentation)

| Role | Email | Password | Details |
| :--- | :--- | :--- | :--- |
| **Patient** | `patient@medibook.com` | `password123` | Demo Patient (Rahul Sharma) |
| **Doctor** | `doctor@medibook.com` | `password123` | Demo Doctor (Dr. Arun Kumar, Cardiologist) |

*(Quick one-click login buttons are also available directly on the Login page!)*

---

## 🚀 Running the Application

### 1. Run the Spring Boot Backend
Navigate to `backend`:
```bash
cd backend
mvnw spring-boot:run
```
*(Backend will start on `http://localhost:8080` with H2 Console at `http://localhost:8080/h2-console`)*

### 2. Run the React Frontend
Navigate to `frontend`:
```bash
cd frontend
npm install
npm run dev
```
*(Frontend will start on `http://localhost:5173`)*

---

## 🗄️ Database Schema & Entities

- `users`: `id`, `name`, `email`, `password`, `phone`, `role`
- `doctors`: `id`, `user_id`, `name`, `specialization`, `experience`, `hospital`, `consultation_fee`, `rating`, `bio`
- `departments`: `id`, `name`, `icon`, `description`, `doctor_count`
- `availability`: `id`, `doctor_id`, `available_date`, `start_time`, `end_time`, `is_booked`
- `appointments`: `id`, `patient_id`, `doctor_id`, `appointment_date`, `appointment_time`, `status`, `notes`
