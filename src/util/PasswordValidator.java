/*
 * HAPA Vehicle Rental System - Password Validation Utility
 * Provides password strength validation functionality
 * Ensures passwords meet security requirements across the application
 * Used in user registration, password changes, and security enforcement
 */
package util;

/**
 * PasswordValidator - Utility class for password validation
 * Provides methods to validate password strength and security requirements
 * Enforces consistent password policies throughout the application
 *
 * @author Pacifique Harerimana
 */

/**
 * Utility class for validating password strength and security
 * Implements comprehensive password validation rules
 */
public class PasswordValidator {
    
    /**
     * Validates password strength according to security requirements
     * Checks for minimum length and character type requirements
     * 
     * Password must contain:
     * - At least 8 characters
     * - At least one lowercase letter
     * - At least one uppercase letter
     * - At least one digit
     * - At least one special character
     * 
     * @param password The password to validate
     * @return true if password meets all requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        // Check for null or minimum length requirement
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Initialize character type flags
        boolean hasLower = false;    // Has lowercase letter
        boolean hasUpper = false;    // Has uppercase letter
        boolean hasDigit = false;    // Has numeric digit
        boolean hasSpecial = false;  // Has special character
        
        // Check each character in the password
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }
        
        // Password is valid only if all requirements are met
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }
    
    /**
     * Returns a formatted string describing password requirements
     * Used for displaying password rules to users in forms and dialogs
     * 
     * @return String containing password requirements description
     */
    public static String getPasswordRequirements() {
        return "Password must contain at least 8 characters with:\n" +
               "• One lowercase letter\n" +
               "• One uppercase letter\n" +
               "• One number\n" +
               "• One special character";
    }
}