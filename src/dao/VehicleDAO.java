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
import model.Vehicle;

public interface VehicleDAO {
    List<Vehicle> getAllVehicles();
    
    public int countVehicles();
    
    public int countAvailableToday();
    
    public List<Vehicle> searchVehicles(String query);
    
    public Vehicle findById(int id);
    
    public boolean deleteVehicle(int id);
    
    public boolean addVehicle(Vehicle vehicle);
    
    public boolean updateVehicle(Vehicle vehicle);
}

