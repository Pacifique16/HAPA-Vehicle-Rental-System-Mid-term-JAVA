/*
 * HAPA Vehicle Rental System - User Data Access Object Implementation
 * Implements all database operations related to user management
 * Provides concrete implementation of UserDAO interface methods
 * Handles user authentication, CRUD operations, and profile management
 */
package dao;

/**
 * UserDAOImpl - Implementation of UserDAO interface
 * Provides concrete database operations for user management
 * Handles authentication, user CRUD operations, and status management
 *
 * @author Pacifique Harerimana
 */

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class for user database operations
 * Provides concrete methods for user authentication and management
 */
public class UserDAOImpl implements UserDAO {

    /**
     * Authenticates user with username and password
     * Checks credentials against database and validates account status
     * Prevents login for inactive accounts
     * 
     * @param username User's username
     * @param password User's password
     * @return User object if authentication successful and account active, null otherwise
     */
    @Override
    public User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        System.out.println("Attempting login with username: " + username);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // Verify database connection
            if (con == null) {
                System.out.println("Database connection is null!");
                return null;
            }

            // Set query parameters
            pst.setString(1, username);
            pst.setString(2, password);
            System.out.println("Executing query: " + sql);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("User found in database!");
                
                // Create user object from database record
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                u.setStatus(rs.getString("status"));
                System.out.println("User role: " + u.getRole());
                
                // Verify account is active
                if ("Inactive".equals(u.getStatus())) {
                    System.out.println("User account is inactive");
                    return null; // Prevent login for inactive users
                }
                
                return u;
            } else {
                System.out.println("No user found with these credentials");
            }

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Authentication failed
    }
    
    
    /**
     * Adds a new user to the database
     * Sets default status to "Active" if not specified
     * 
     * @param user User object containing user information
     * @return true if user was successfully added, false otherwise
     */
    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, phone, email, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // Set all user parameters
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getFullName());
            pst.setString(4, user.getPhone());
            pst.setString(5, user.getEmail());
            pst.setString(6, user.getRole());
            pst.setString(7, user.getStatus() != null ? user.getStatus() : "Active");

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates user profile information (name, phone, email)
     * Does not update username, password, role, or status
     * 
     * @param user User object with updated profile information
     * @return true if profile was successfully updated, false otherwise
     */
    @Override
    public boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, email = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // Set profile update parameters
            pst.setString(1, user.getFullName());
            pst.setString(2, user.getPhone());
            pst.setString(3, user.getEmail());
            pst.setInt(4, user.getId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Changes user password after verifying current password
     * Requires old password verification for security
     * 
     * @param id User ID
     * @param oldP Current password for verification
     * @param newP New password to set
     * @return true if password was successfully changed, false otherwise
     */
    @Override
    public boolean changePassword(int id, String oldP, String newP) {
        String sql = "UPDATE users SET password = ? WHERE id = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // Set password change parameters with verification
            pst.setString(1, newP);  // New password
            pst.setInt(2, id);       // User ID
            pst.setString(3, oldP);  // Old password for verification

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


@Override
public List<User> getAllUsers() {

    List<User> list = new ArrayList<>();
    String sql = "SELECT * FROM users";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setFullName(rs.getString("full_name"));
            u.setPhone(rs.getString("phone"));
            u.setEmail(rs.getString("email"));
            u.setRole(rs.getString("role"));
            u.setStatus(rs.getString("status"));

            list.add(u);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    @Override
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, full_name = ?, phone = ?, email = ?, role = ?, status = ?";
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            sql += ", password = ?";
        }
        sql += " WHERE id = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getFullName());
            pst.setString(3, user.getPhone());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getRole());
            pst.setString(6, user.getStatus() != null ? user.getStatus() : "Active");
            
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                pst.setString(7, user.getPassword());
                pst.setInt(8, user.getId());
            } else {
                pst.setInt(7, user.getId());
            }
            
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                u.setStatus(rs.getString("status"));
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Alternative authentication method (delegates to login)
     * Provides additional authentication entry point
     * 
     * @param username User's username
     * @param password User's password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        return login(username, password);
    }
    
    @Override
    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, userId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
