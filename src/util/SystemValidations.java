/*
 * HAPA Vehicle Rental System - Comprehensive System Validations
 * Centralized validation class containing all business and technical validation rules
 * Used throughout the system to ensure data integrity and business rule compliance
 * 
 * This class consolidates all validations for easy demonstration and maintenance
 */
package util;

import dao.BookingDAO;
import dao.BookingDAOImpl;
import dao.UserDAO;
import dao.UserDAOImpl;
import dao.VehicleDAO;
import dao.VehicleDAOImpl;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * SystemValidations - Centralized validation utility class
 * Contains all business and technical validation rules used in the HAPA Vehicle Rental System
 * Provides static methods for validating user input, business rules, and data integrity
 *
 * @author Pacifique Harerimana
 */
public class SystemValidations {
    
    // DAO instances for database validations
    private static final UserDAO userDAO = new UserDAOImpl();
    private static final VehicleDAO vehicleDAO = new VehicleDAOImpl();
    private static final BookingDAO bookingDAO = new BookingDAOImpl();
    
    // Regular expression patterns for technical validations
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern RWANDA_PHONE_PATTERN = Pattern.compile("^07\\d{8}$");
    private static final Pattern PLATE_NUMBER_PATTERN = Pattern.compile("^[A-Z]{3}\\s?\\d{3}[A-Z]?$");
    
    // ===========================================
    // TECHNICAL VALIDATIONS (5+ Required)
    // ===========================================
    
    /**
     * TECHNICAL VALIDATION 1: Password Strength Validation
     * Validates password meets security requirements:
     * - At least 8 characters
     * - Contains uppercase letter
     * - Contains lowercase letter
     * - Contains digit
     * - Contains special character
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }
    
    /**
     * TECHNICAL VALIDATION 2: Email Format Validation
     * Validates email address format using regex pattern
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * TECHNICAL VALIDATION 3: Rwandan Phone Number Validation
     * Validates phone number follows Rwanda format: 07xxxxxxxx
     */
    public static boolean isValidRwandanPhone(String phone) {
        return phone != null && RWANDA_PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * TECHNICAL VALIDATION 4: Vehicle Plate Number Format Validation
     * Validates plate number follows standard format: ABC 123A
     */
    public static boolean isValidPlateNumber(String plateNumber) {
        return plateNumber != null && PLATE_NUMBER_PATTERN.matcher(plateNumber.toUpperCase()).matches();
    }
    
    /**
     * TECHNICAL VALIDATION 5: Numeric Value Validation
     * Validates that price values are positive numbers
     */
    public static boolean isValidPrice(double price) {
        return price > 0 && price <= 999999.99; // Reasonable price range
    }
    
    /**
     * TECHNICAL VALIDATION 6: Date Format and Range Validation
     * Validates date is not null and within reasonable range
     */
    public static boolean isValidDate(Date date) {
        if (date == null) return false;
        
        Date now = new Date();
        Date maxFutureDate = new Date(now.getTime() + (365L * 24 * 60 * 60 * 1000)); // 1 year from now
        
        return date.after(new Date(0)) && date.before(maxFutureDate);
    }
    
    /**
     * TECHNICAL VALIDATION 7: String Length and Content Validation
     * Validates string fields meet minimum requirements
     */
    public static boolean isValidStringField(String value, int minLength, int maxLength) {
        return value != null && 
               value.trim().length() >= minLength && 
               value.trim().length() <= maxLength &&
               !value.trim().isEmpty();
    }
    
    // ===========================================
    // BUSINESS VALIDATIONS (5+ Required)
    // ===========================================
    
    /**
     * BUSINESS VALIDATION 1: Vehicle Availability for Booking
     * Ensures vehicle is not already booked for the requested dates
     * Prevents double-booking of vehicles
     */
    public static boolean isVehicleAvailableForBooking(int vehicleId, Date startDate, Date endDate) {
        // Check if vehicle exists and is not in maintenance
        if (!vehicleDAO.isVehicleAvailableForBooking(vehicleId)) {
            return false;
        }
        
        // Check for date conflicts with existing bookings
        return !bookingDAO.isVehicleUnavailable(vehicleId, startDate, endDate);
    }
    
    /**
     * BUSINESS VALIDATION 2: Booking Date Logic Validation
     * Ensures start date is before end date and both are in the future
     */
    public static boolean isValidBookingDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return false;
        
        Date today = new Date();
        Date todayStart = new Date(today.getYear(), today.getMonth(), today.getDate());
        
        return startDate.before(endDate) && 
               !startDate.before(todayStart) && // Start date not in the past
               !endDate.before(todayStart);     // End date not in the past
    }
    
    /**
     * BUSINESS VALIDATION 3: User Role-Based Access Control
     * Validates user has appropriate role for requested operation
     */
    public static boolean hasValidRoleForOperation(String userRole, String requiredRole) {
        if (userRole == null || requiredRole == null) return false;
        
        // Admin can access everything, customers only customer operations
        return "admin".equals(userRole) || userRole.equals(requiredRole);
    }
    
    /**
     * BUSINESS VALIDATION 4: Duplicate Booking Prevention
     * Prevents same customer from booking same vehicle for same dates
     */
    public static boolean isNotDuplicateBooking(int customerId, int vehicleId, Date startDate, Date endDate) {
        return !bookingDAO.isDuplicateBooking(customerId, vehicleId, startDate, endDate);
    }
    
    /**
     * BUSINESS VALIDATION 5: Booking Status Workflow Validation
     * Ensures booking status transitions follow business rules
     */
    public static boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) return false;
        
        switch (currentStatus) {
            case "PENDING":
                return "APPROVED".equals(newStatus) || "REJECTED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "APPROVED":
                return "CANCELLED".equals(newStatus) || "EXPIRED".equals(newStatus);
            case "REJECTED":
            case "CANCELLED":
            case "EXPIRED":
                return false; // Final states - no transitions allowed
            default:
                return false;
        }
    }
    
    /**
     * BUSINESS VALIDATION 6: User Account Status Validation
     * Ensures only active users can perform operations
     */
    public static boolean isActiveUser(String userStatus) {
        return "Active".equals(userStatus);
    }
    
    /**
     * BUSINESS VALIDATION 7: Vehicle Maintenance Status Validation
     * Prevents booking of vehicles under maintenance
     */
    public static boolean isVehicleNotInMaintenance(String vehicleStatus) {
        return !"Maintenance".equals(vehicleStatus);
    }
    
    /**
     * BUSINESS VALIDATION 8: Booking Duration Validation
     * Ensures booking duration is within acceptable limits (1-30 days)
     */
    public static boolean isValidBookingDuration(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return false;
        
        long diffInMillis = endDate.getTime() - startDate.getTime();
        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
        
        return diffInDays >= 1 && diffInDays <= 30; // 1 to 30 days
    }
    
    // ===========================================
    // COMPOSITE VALIDATION METHODS
    // ===========================================
    
    /**
     * Complete User Registration Validation
     * Combines multiple technical and business validations for user signup
     */
    public static String validateUserRegistration(String fullName, String username, String phone, 
                                                 String email, String password, String confirmPassword) {
        
        // Technical validations
        if (!isValidStringField(fullName, 2, 100)) {
            return "Full name must be between 2 and 100 characters";
        }
        
        if (!isValidStringField(username, 3, 50)) {
            return "Username must be between 3 and 50 characters";
        }
        
        if (!isValidRwandanPhone(phone)) {
            return "Invalid Rwandan phone number format (07xxxxxxxx)";
        }
        
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        
        if (!isValidPassword(password)) {
            return "Password must contain at least 8 characters with uppercase, lowercase, digit, and special character";
        }
        
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        
        return null; // All validations passed
    }
    
    /**
     * Complete Vehicle Registration Validation
     * Combines multiple technical validations for vehicle addition
     */
    public static String validateVehicleRegistration(String plateNumber, String model, String category,
                                                   double pricePerDay, String fuelType, String transmission, int seats) {
        
        if (!isValidPlateNumber(plateNumber)) {
            return "Invalid plate number format";
        }
        
        if (!isValidStringField(model, 2, 50)) {
            return "Vehicle model must be between 2 and 50 characters";
        }
        
        if (!isValidStringField(category, 2, 30)) {
            return "Vehicle category must be between 2 and 30 characters";
        }
        
        if (!isValidPrice(pricePerDay)) {
            return "Price per day must be a positive number";
        }
        
        if (seats < 1 || seats > 50) {
            return "Number of seats must be between 1 and 50";
        }
        
        return null; // All validations passed
    }
    
    /**
     * Complete Booking Validation
     * Combines multiple business and technical validations for booking creation
     */
    public static String validateBookingCreation(int customerId, int vehicleId, Date startDate, Date endDate) {
        
        // Technical validations
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            return "Invalid date format or range";
        }
        
        // Business validations
        if (!isValidBookingDateRange(startDate, endDate)) {
            return "Start date must be before end date and both must be in the future";
        }
        
        if (!isValidBookingDuration(startDate, endDate)) {
            return "Booking duration must be between 1 and 30 days";
        }
        
        if (!isVehicleAvailableForBooking(vehicleId, startDate, endDate)) {
            return "Vehicle is not available for the selected dates";
        }
        
        if (!isNotDuplicateBooking(customerId, vehicleId, startDate, endDate)) {
            return "You have already booked this vehicle for these dates";
        }
        
        return null; // All validations passed
    }
    
    /**
     * Get Password Requirements Message
     * Returns formatted string describing password requirements
     */
    public static String getPasswordRequirements() {
        return "Password must contain at least 8 characters with:\n" +
               "• One lowercase letter\n" +
               "• One uppercase letter\n" +
               "• One number\n" +
               "• One special character";
    }
    
    /**
     * Get All Validation Rules Summary
     * Returns a comprehensive list of all validation rules for demonstration
     */
    public static String getAllValidationRules() {
        return "=== HAPA VEHICLE RENTAL SYSTEM VALIDATION RULES ===\n\n" +
               
               "TECHNICAL VALIDATIONS:\n" +
               "1. Password Strength: 8+ chars, upper, lower, digit, special\n" +
               "2. Email Format: Valid email pattern with @ and domain\n" +
               "3. Phone Format: Rwanda format 07xxxxxxxx\n" +
               "4. Plate Number: Standard format ABC 123A\n" +
               "5. Price Range: Positive numbers within reasonable range\n" +
               "6. Date Range: Valid dates within acceptable timeframe\n" +
               "7. String Length: Minimum/maximum character limits\n\n" +
               
               "BUSINESS VALIDATIONS:\n" +
               "1. Vehicle Availability: No double-booking prevention\n" +
               "2. Booking Dates: Start before end, future dates only\n" +
               "3. Role-Based Access: User permissions by role\n" +
               "4. Duplicate Prevention: Same customer/vehicle/dates\n" +
               "5. Status Workflow: Valid booking status transitions\n" +
               "6. Account Status: Only active users can operate\n" +
               "7. Maintenance Status: No booking of maintenance vehicles\n" +
               "8. Duration Limits: 1-30 day booking duration\n";
    }
}