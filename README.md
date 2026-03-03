# HAPA Vehicle Rental System

A comprehensive Java-based desktop application for managing vehicle rentals with separate interfaces for customers and administrators.

<img width="545" height="605" alt="login" src="https://github.com/user-attachments/assets/da287dfc-1f3f-403e-a02e-38ae4481f4f3" />

## 🚗 Project Overview

**HAPA Vehicle Rental System** is a real-world problem-solving application that addresses vehicle rental management challenges. The system provides a complete solution for vehicle booking, user management, and rental administration using modern software design patterns.

## 🏗️ System Architecture

- **Language**: Java (JDK 8+)
- **GUI Framework**: Java Swing
- **Database**: PostgreSQL
- **Architecture**: 3-Tier (Presentation, Business, Data Access)
- **Design Patterns**: MVC, DAO, Singleton
- **IDE**: NetBeans

## 📊 Database Schema

**Database Name**: `hapa_vehicle_rental_system_db`

### Tables (4 Primary Relations):
1. **users** - User accounts (customers and admins)
   - Attributes: id, username, password, full_name, phone, email, role, status
2. **vehicles** - Vehicle inventory management
   - Attributes: id, plate_number, model, category, price_per_day, image_path, fuel_type, transmission, seats, status
3. **bookings** - Rental transactions and booking management
   - Attributes: id, customer_id, vehicle_id, start_date, end_date, total_cost, status, rejection_reason

## ✅ Validation Rules

### Technical Validations (7):
1. **Password Strength** - 8+ characters with uppercase, lowercase, digit, special character
2. **Email Format** - Valid email pattern validation
3. **Phone Number** - Rwanda format (07xxxxxxxx)
4. **Plate Number** - Standard vehicle plate format
5. **Price Range** - Positive numbers within reasonable limits
6. **Date Range** - Valid dates within acceptable timeframe
7. **String Length** - Minimum/maximum character validation

### Business Validations (8):
1. **Vehicle Availability** - Prevents double-booking of vehicles
2. **Booking Date Logic** - Start date before end date, future dates only
3. **Role-Based Access** - User permission validation by role
4. **Duplicate Prevention** - Same customer/vehicle/date combinations
5. **Status Workflow** - Valid booking status transitions
6. **Account Status** - Only active users can perform operations
7. **Maintenance Status** - Prevents booking of maintenance vehicles
8. **Duration Limits** - 1-30 day booking duration validation

## 🎯 Key Features

### For Customers:
- **User Registration** with comprehensive validation
- **Vehicle Browsing** with responsive card layout
- **Booking Management** with date selection and conflict detection
- **Booking History** with filtering and status tracking
- **Profile Management** and settings

### For Administrators:
- **Dashboard Analytics** with key metrics and charts
- **Vehicle Management** - Add, edit, delete, status management
- **User Administration** - Manage customer and admin accounts
- **Booking Approval** - Review and approve/reject bookings
- **Comprehensive Reports** - Active rentals, booking history, analytics
- **CSV Export** functionality for reports

## 🖥️ User Interface

### GUI Pages (12+):
1. **LoginForm** - Authentication interface
2. **SignupForm** - User registration
3. **AdminDashboard** - Admin main interface
4. **CustomerDashboard** - Customer main interface
5. **AdminHomePanel** - Dashboard with analytics
6. **BookVehiclePanel** - Vehicle browsing and selection
7. **MyBookingsPanel** - Customer booking management
8. **BookingsApprovalPanel** - Admin booking approval
9. **ManageUsersPanel** - User administration
10. **ManageVehiclesPanel** - Vehicle management
11. **AdminReportsPanel** - Reports and analytics
12. **SettingsPanel** - User profile management

## 🔧 CRUD Operations

### Implemented with DAO Pattern:
- **Create**: Add users, vehicles, bookings
- **Read**: Display data in JTables with filtering and pagination
- **Update**: Edit profiles, approve bookings, update vehicle status
- **Delete**: Remove users, vehicles, cancel bookings

## 📁 Project Structure

```
HAPA_Vehicle_Rental_System/
├── src/
│   ├── dao/                    # Data Access Objects
│   │   ├── BookingDAO.java
│   │   ├── BookingDAOImpl.java
│   │   ├── UserDAO.java
│   │   ├── UserDAOImpl.java
│   │   ├── VehicleDAO.java
│   │   ├── VehicleDAOImpl.java
│   │   └── DBConnection.java
│   ├── model/                  # Entity Classes
│   │   ├── User.java
│   │   ├── Vehicle.java
│   │   ├── Booking.java
│   │   └── BookingRecord.java
│   ├── util/                   # Utility Classes
│   │   ├── PasswordValidator.java
│   │   └── SystemValidations.java
│   └── view/                   # GUI Components
│       ├── LoginForm.java
│       ├── SignupForm.java
│       ├── AdminDashboard.java
│       ├── CustomerDashboard.java
│       └── [Other UI Classes]
├── images/                     # Application resources
├── COMPLETE_CODE_DOCUMENTATION.md
└── README.md
```

## 🚀 Installation & Setup

### Prerequisites:
- Java JDK 8 or higher
- PostgreSQL database server
- NetBeans IDE (recommended)
- PostgreSQL JDBC Driver

### Database Setup:
1. Install PostgreSQL
2. Create database: `hapa_vehicle_rental_system_db`
3. Update connection details in `DBConnection.java` if needed
4. Run the application to auto-create tables

### Running the Application:
1. Clone the repository
2. Open project in NetBeans
3. Ensure PostgreSQL is running
4. Add PostgreSQL JDBC driver to classpath
5. Run `LoginForm.java` as main class

## 👥 User Accounts

### Default Admin Account:
- **Username**: admin
- **Password**: admin123
- **Role**: Administrator

### Customer Registration:
- Use the "Sign Up" option in LoginForm
- Complete validation required for registration

## 🔒 Security Features

- **Password Encryption** and strength validation
- **Role-based Access Control** (RBAC)
- **Input Validation** and sanitization
- **SQL Injection Prevention** with PreparedStatements
- **Session Management** with user context

## 📊 Reporting Features

- **Dashboard Analytics** with real-time metrics
- **Active Rentals Report** - Currently ongoing bookings
- **Booking History Report** - Complete rental history
- **Vehicle Availability Report** - Fleet status overview
- **Most Rented Vehicles** - Popular vehicle analytics
- **CSV Export** for all reports

## 🎨 UI/UX Features

- **Modern Swing Interface** with custom styling
- **Responsive Design** with wrap layouts
- **Color-coded Status Indicators** for easy recognition
- **Placeholder Text** for better user experience
- **Hover Effects** and interactive elements
- **JOptionPane Messages** for user feedback

## 🧪 Testing & Validation

- **Comprehensive Input Validation** (15 validation rules)
- **Business Rule Enforcement** throughout the system
- **Error Handling** with user-friendly messages
- **Data Integrity** checks and constraints

## 📚 Documentation

- **Complete Code Documentation** - `COMPLETE_CODE_DOCUMENTATION.md`
- **JavaDoc Comments** throughout codebase
- **Inline Comments** explaining complex logic
- **System Architecture** documentation

## 👨‍💻 Author

**Pacifique Harerimana**
- Student Project - Mid-Semester Assignment
- Java Programming Course

 📧 Contact: [GitHub](https://github.com/Pacifique16)

## 📝 License

© Copyright 2025 Pacifique Harerimana

This project is for educational purposes as part of AUCA Java Programming coursework. Feel free to fork and learn from it, but please give credit where it's due.



## ⭐ Show Your Support

**If you found this project helpful or interesting, please consider giving it a star!** 🌟

Your support motivates me to create more educational projects and helps others discover useful resources.


##
*Built with ❤️ for learning and sharing knowledge*
