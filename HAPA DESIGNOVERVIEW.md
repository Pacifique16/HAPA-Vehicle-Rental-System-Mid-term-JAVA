# ✅ HAPA Vehicle Rental System – Final Design Overview

## **1. Login & Signup**

### ✔ Login Form
- Shared by both **Admin** and **Customer**.  
- “**I’m a staff**” option clearly distinguishes roles.  
- Uses **username & password** authentication.  
- Directs users to respective dashboards upon successful login.

### ✔ Signup Form (Customers only)
- Fields: **Full name**, **Email**, **Phone number**, **Username**, **Password**.  
- On successful registration → user stored in the **`customers`** table.  
- Clean, branded design featuring the **HAPA logo and theme**.

<br><br>

## **2. Customer Panel**

### I. Book a Vehicle
- Displays all available cars in a **grid layout**.  
- Shows **image, model, and price per day**.  
- Includes a **“Book Now”** button for each car.  
- Opens a booking dialog with:
  - **Pickup date** and **Drop-off date**.  
  - Calculates total amount dynamically (**price_per_day × days**).  
  - Saves booking to the **database**.

### II. My Bookings
- Displays the customer’s **current and past rentals**.  
- Allows **cancellation** of bookings via **plate number input**.  
- Shows **vehicle image, model, price, and booking status**.  
- Clean layout with **easy navigation** and **logout** options.

---
<br>

## **3. Admin Panel**

### I. Manage Vehicles (CRUD)
- Add, edit, delete, or search vehicles by **plate number**.  
- Fields: **Plate number, Model, Category, Price/day, Image**.  
- Real-time data displayed in a **JTable-style view**.  
- Buttons for **Add New**, **Update**, **Delete**, and **Search**.

### II. Manage Users (Customers)
- Add, edit, or remove **customer accounts**.  
- Fields: **Full name, Phone, Email, Username, Password**.  
- Displays existing users in a **JTable** for quick management.  
- Streamlined controls (**Add / Edit / Delete**) for user handling.

### III. Reports
- Three key report tabs integrated into the Admin panel:
  1. **Active Rentals:** Ongoing bookings with total cost and dates.  
  2. **Most Rented Vehicles:** Shows vehicle popularity and total income.  
  3. **Vehicle Availability:** Displays which vehicles are “Available” or “Rented.”  
- Each report uses **structured tables**, ideal for implementation with `JTable`.

### IV. Settings
- Accessible to both **Admin** and **Customer**.  
- **Profile Info:** View name, email, contact, and username.  
- **Change Password:** Enter old PIN, new PIN, and confirm new PIN.  
- Clear, professional design — easy to implement using **Swing tabs** or **separate frames**.

<br><br>

## **4. System Flow Summary**

| Role | Access | Main Actions |
|------|---------|--------------|
| **Admin** | Full access | Manage vehicles, users, view reports, change password |
| **Customer** | Limited | Book vehicles, view bookings, manage profile |
| **Shared Features** | – | Login, Logout, Settings (Profile & Password) |

<br>

### 🧱 **Note**
This design follows the **MVC (Model-View-Controller)** and **DAO (Data Access Object)** architecture patterns.  
The goal is to ensure a clear separation between the user interface, business logic, and database operations.
