/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author Pacifique Harerimana
 */

import java.util.List;
import model.User;

public interface UserDAO {
    User login(String username, String password);

    boolean addUser(User user);

    public boolean changePassword(int id, String oldP, String newP);

    public boolean updateUserProfile(User user);

    public int countUsers();

    public boolean deleteUser(int id);

    public boolean updateUser(User uu);

    public User findById(int id);

    public List<User> getAllUsers();
    
}

