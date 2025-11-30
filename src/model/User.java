/*
 * HAPA Vehicle Rental System - User Model Class
 * Represents a user entity in the system (customer or admin)
 * Contains all user-related information and properties
 * Used for authentication, profile management, and user administration
 */
package model;

/**
 * User - Model class representing a system user
 * Contains user information including credentials, personal details, and role
 * Supports both customer and admin user types with status management
 *
 * @author Pacifique Harerimana
 */

/**
 * User entity class representing system users
 * Stores user credentials, personal information, role, and status
 */
public class User {
    
    // User identification and credentials
    private int id;              // Unique user identifier
    private String username;     // Login username
    private String password;     // User password (hashed in database)
    
    // Personal information
    private String fullName;     // User's full name
    private String phone;        // Contact phone number
    private String email;        // Email address
    
    // System properties
    private String role;         // User role: "admin" or "customer"
    private String status;       // Account status: "Active" or "Inactive"

    /**
     * Default constructor
     * Creates an empty User object
     */
    public User() {}

    /**
     * Parameterized constructor
     * Creates a User object with specified values
     * 
     * @param id User ID
     * @param username Login username
     * @param password User password
     * @param fullName Full name of the user
     * @param phone Phone number
     * @param email Email address
     * @param role User role (admin/customer)
     */
    public User(int id, String username, String password, String fullName, String phone, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    // Getter and Setter methods for all properties

    /**
     * Gets the user ID
     * @return User ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user ID
     * @param id User ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the username
     * @return Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     * @param username Username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password
     * @return User password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password
     * @param password Password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the full name
     * @return User's full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name
     * @param fullName Full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the phone number
     * @return Phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number
     * @param phone Phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the email address
     * @return Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address
     * @param email Email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user role
     * @return User role (admin/customer)
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user role
     * @param role Role to set (admin/customer)
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Gets the user status
     * @return User status (Active/Inactive)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the user status
     * @param status Status to set (Active/Inactive)
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
