package com.hospital.model;

import java.sql.Date;

public class Ambulance {
    private int ambulanceId;
    private String vehicleNumber;
    private String ambulanceType;
    private String model;
    private int year;
    private String driverName;
    private String driverPhone;
    private String driverLicense;
    private String status;
    private Date lastMaintenanceDate;
    private Date nextMaintenanceDate;

    // Constructors
    public Ambulance() {
    }

    public Ambulance(int ambulanceId, String vehicleNumber, String ambulanceType,
            String model, int year, String driverName, String driverPhone,
            String driverLicense, String status, Date lastMaintenanceDate,
            Date nextMaintenanceDate) {
        this.ambulanceId = ambulanceId;
        this.vehicleNumber = vehicleNumber;
        this.ambulanceType = ambulanceType;
        this.model = model;
        this.year = year;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.driverLicense = driverLicense;
        this.status = status;
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    // Getters and Setters
    public int getAmbulanceId() {
        return ambulanceId;
    }

    public void setAmbulanceId(int ambulanceId) {
        this.ambulanceId = ambulanceId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getAmbulanceType() {
        return ambulanceType;
    }

    public void setAmbulanceType(String ambulanceType) {
        this.ambulanceType = ambulanceType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(Date lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public Date getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(Date nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    @Override
    public String toString() {
        return vehicleNumber + " - " + ambulanceType;
    }
}