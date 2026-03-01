# 📦 Swift Ship: End-to-End Delivery Management System

> **A comprehensive desktop application designed to revolutionize courier logistics through centralized automation, real-time tracking, and role-based workflows.**

---

## 🚀 Project Overview

[cite_start]**Swift Ship** is a JavaFX-based solution developed by **Innovora** to modernize logistics for small-to-medium-sized courier companies[cite: 3, 25]. [cite_start]It addresses critical industry pain points such as manual data entry errors, fragmented rider communication, and lack of transparency in parcel tracking [cite: 26-29].

[cite_start]By digitizing the entire delivery lifecycle, Swift Ship provides a seamless experience for customers while offering powerful management tools for administrators, dispatchers, and warehouse staff [cite: 30-31].

### 🌟 Key Features
* [cite_start]**7 Distinct User Roles:** Specialized dashboards for Customers, Managers, Dispatchers, Pickup/Delivery Riders, Warehouse Staff, and Admins[cite: 37, 443].
* [cite_start]**Real-Time Workflow:** Automated transition of orders from *Creation* → *Approval* → *Pickup* → *Warehouse* → *Delivery* [cite: 36, 538-544].
* [cite_start]**Smart Validation:** Managers review shipment photos and payment status before approving orders to prevent fraud[cite: 471, 477].
* [cite_start]**Visual Analytics:** Comprehensive reporting on revenue, rider performance, and delivery success rates [cite: 39, 548-551].
* [cite_start]**SOLID Architecture:** Built using Layered Architecture and MVC patterns to ensure scalability and maintainability[cite: 148, 407].

---

## 🔄 System Architecture & Design

### 1. High-Level Ecosystem
[cite_start]Swift Ship operates within a broader ecosystem, integrating with Payment Gateways for transactions, SMS/Email services for notifications, and GPS services for real-time location tracking [cite: 78-79].

![System Context Diagram](1-system-context.png)

### 2. The Order Lifecycle
The core of our business logic is the **8-Phase Order Lifecycle**. [cite_start]Strict state transitions ensure a parcel cannot be delivered before it is picked up or processed [cite: 136-144].

![Order Lifecycle Workflow](2-order-lifecycle.png)

---

## 🧩 Functional Modules

### Use Case Overview
The system is divided into clear functional modules catering to our 7 user actors.
![Use Case Grid](3-use-case-grid.png)

### Interaction Model
A detailed look at how different actors (Customer, Manager, Dispatcher) interact with the core system boundaries.
![Use Case UML Diagram](4-use-case-uml.png)

---

## 🏗️ Technical Architecture

[cite_start]We utilized a **Layered Architecture** to separate concerns, ensuring that the UI (JavaFX) is decoupled from the Business Logic and Data Access layers [cite: 147-148].

### 1. Layered Architecture
* [cite_start]**Presentation Layer:** JavaFX FXML views and Controllers [cite: 204-206].
* [cite_start]**Application Layer:** Handles flow control and UI state [cite: 209-210].
* [cite_start]**Business Layer:** Domain models (Order, User, Parcel) and business rules [cite: 214-216].
* [cite_start]**Data Access Layer:** DAO pattern implementation for database persistence [cite: 219-222].

![Layered Architecture](5-layered-arch.png)

### 2. Component Design
[cite_start]Major system components include Authentication, Order Management, Payment Processing, Assignment Coordination, and Reporting [cite: 329-354].

![Component Diagram](6-component-diagram.png)

### 3. Deployment Strategy
The application is deployed using a Client-Server model. [cite_start]Client machines run the JavaFX desktop app, communicating with a central MySQL Database Server and File Storage for parcel images [cite: 396-401].

![Deployment Architecture](7-deployment-arch.png)

---

## 🛠️ Technology Stack

| Category | Technology Used | Description |
| :--- | :--- | :--- |
| **Frontend** | JavaFX, FXML, CSS | [cite_start]Rich desktop UI with declarative design [cite: 44-45] |
| **Backend** | Java 8+ | [cite_start]Core business logic and service layer [cite: 47] |
| **Database** | MySQL 8.0 | [cite_start]Relational data storage with ACID compliance [cite: 51, 572] |
| **Persistence** | JDBC & DAO Pattern | [cite_start]Efficient data access and separation of concerns [cite: 48, 52] |
| **Design** | MVC & DDD | [cite_start]Model-View-Controller and Domain-Driven Design [cite: 212, 217] |

---

## 👨‍💻 The Team (Innovora)

[cite_start]Developed for the **Software Design and Architecture** course at **FAST-NUCES**[cite: 8].

* [cite_start]**Talha Shafi** (23i-0563) - *Team Lead & Full Stack Developer* [cite: 5]
* [cite_start]**Hannan Abid** (23i-0713) - *Frontend & UI/UX Specialist* [cite: 6]
* [cite_start]**Abdul Mahid** (23i-0828) - *Backend & Database Engineer* [cite: 7]

---

*© 2025 Innovora. All Rights Reserved.*
