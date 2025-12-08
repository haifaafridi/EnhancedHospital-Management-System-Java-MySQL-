package com.hospital.model;

import java.sql.Timestamp;

public class MedicalHistory {
    private int historyId;
    private int patientId;
    private String patientName; // For display purposes
    private String allergies;
    private String chronicConditions;
    private String previousSurgeries;
    private String currentMedications;
    private String familyMedicalHistory;
    private String smokingStatus;
    private String alcoholConsumption;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors
    public MedicalHistory() {
    }

    public MedicalHistory(int historyId, int patientId, String patientName, String allergies,
            String chronicConditions, String previousSurgeries, String currentMedications,
            String familyMedicalHistory, String smokingStatus, String alcoholConsumption,
            String notes, Timestamp createdAt, Timestamp updatedAt) {
        this.historyId = historyId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.allergies = allergies;
        this.chronicConditions = chronicConditions;
        this.previousSurgeries = previousSurgeries;
        this.currentMedications = currentMedications;
        this.familyMedicalHistory = familyMedicalHistory;
        this.smokingStatus = smokingStatus;
        this.alcoholConsumption = alcoholConsumption;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getChronicConditions() {
        return chronicConditions;
    }

    public void setChronicConditions(String chronicConditions) {
        this.chronicConditions = chronicConditions;
    }

    public String getPreviousSurgeries() {
        return previousSurgeries;
    }

    public void setPreviousSurgeries(String previousSurgeries) {
        this.previousSurgeries = previousSurgeries;
    }

    public String getCurrentMedications() {
        return currentMedications;
    }

    public void setCurrentMedications(String currentMedications) {
        this.currentMedications = currentMedications;
    }

    public String getFamilyMedicalHistory() {
        return familyMedicalHistory;
    }

    public void setFamilyMedicalHistory(String familyMedicalHistory) {
        this.familyMedicalHistory = familyMedicalHistory;
    }

    public String getSmokingStatus() {
        return smokingStatus;
    }

    public void setSmokingStatus(String smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    public String getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(String alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "MedicalHistory{" +
                "historyId=" + historyId +
                ", patientId=" + patientId +
                ", patientName='" + patientName + '\'' +
                ", allergies='" + allergies + '\'' +
                ", chronicConditions='" + chronicConditions + '\'' +
                '}';
    }
}