/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Pacifique Harerimana
 */

public class BookingRecord {
    private Booking booking;
    private Vehicle vehicle;

    public BookingRecord() {}

    public BookingRecord(Booking booking, Vehicle vehicle) {
        this.booking = booking;
        this.vehicle = vehicle;
    }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}
