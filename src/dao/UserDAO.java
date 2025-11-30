/*
 * HAPA Vehicle Rental System - User Data Access Object Interface
 * Defines all database operations related to user management
 * Provides comprehensive user management functionality including authentication,
 * CRUD operations, profile management, and user administration
 */
package dao;

/**
 * UserDAO - Data Access Object interface for user operations
 * Defines contract for all user-related database operations
 * Supports authentication, user management, and profile operations
 *
 * @author Pacifique Harerimana
 */

import java.util.List;
import model.User;

/**
 * Interface defining all user-related database operations
 * Provides methods for authentication, user management, and profile operations
 */
public interface UserDAO {
    /**
     * Authenticates user with username and password
     * @param username User's username
     * @param password User's password
     * @return User object if authentication successful, null otherwise
     */
    User login(String username, String password);

    /**
     * Adds a new user to the database
     * @param user User object to be added
     * @return true if user was successfully added, false otherwise
     */
    boolean addUser(User user);

    /**
     * Changes user's password after verifying old password
     * @param id User ID
     * @param oldP Current password for verification
     * @param newP New password to set
     * @return true if password was successfully changed, false otherwise
     */
    public boolean changePassword(int id, String oldP, String newP);

    /**
     * Updates user profile information
     * @param user User object with updated profile information
     * @return true if profile was successfully updated, false otherwise
     */
    public boolean updateUserProfile(User user);

    /**
     * Counts total number of users in the system
     * @return Total count of users
     */
    public int countUsers();

    /**
     * Deletes a user from the database
     * @param id ID of the user to delete
     * @return true if user was successfully deleted, false otherwise
     */
    public boolean deleteUser(int id);

    /**
     * Updates user information (admin function)
     * @param uu User object with updated information
     * @return true if user was successfully updated, false otherwise
     */
    public boolean updateUser(User uu);

    /**
     * Finds user by their ID
     * @param id User ID to search for
     * @return User object if found, null otherwise
     */
    public User findById(int id);

    /**
     * Retrieves all users from the database
     * @return List of all users
     */
    public List<User> getAllUsers();
    
    /**
     * Updates user status (active/inactive)
     * @param userId ID of the user
     * @param status New status to set
     * @return true if status was successfully updated, false otherwise
     */
    boolean updateUserStatus(int userId, String status);
    
}

