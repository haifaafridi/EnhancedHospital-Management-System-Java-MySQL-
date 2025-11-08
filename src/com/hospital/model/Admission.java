
package com.hospital.model;

import java.math.BigDecimal;
import java.sql.*;

public class Admission {
    private int admissionId;
    private int patientId;
    private int roomId;
    private int admittingDoctorId;
    private Timestamp admissionDate;
    private Date expectedDischargeDate;
    private Timestamp actualDischargeDate;
    private String diagnosis;
    private String admissionType;
    private BigDecimal initialDeposit;
    private String status;
    private String dischargeSummary;
    private String dischargeInstructions;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Admission() {
        this.initialDeposit = BigDecimal.ZERO;
    }

    public Admission(int patientId, int roomId, int admittingDoctorId, String diagnosis) {
        this.patientId = patientId;
        this.roomId = roomId;
        this.admittingDoctorId = admittingDoctorId;
        this.diagnosis = diagnosis;
        this.initialDeposit = BigDecimal.ZERO;
    }

    public int getAdmissionId() {
        return admissionId;
    }

    public void setAdmissionId(int admissionId) {
        this.admissionId = admissionId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getAdmittingDoctorId() {
        return admittingDoctorId;
    }

    public void setAdmittingDoctorId(int admittingDoctorId) {
        this.admittingDoctorId = admittingDoctorId;
    }

    public Timestamp getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Timestamp admissionDate) {
        this.admissionDate = admissionDate;
    }

    public Date getExpectedDischargeDate() {
        return expectedDischargeDate;
    }

    public void setExpectedDischargeDate(Date expectedDischargeDate) {
        this.expectedDischargeDate = expectedDischargeDate;
    }

    public Timestamp getActualDischargeDate() {
        return actualDischargeDate;
    }

    public void setActualDischargeDate(Timestamp actualDischargeDate) {
        this.actualDischargeDate = actualDischargeDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getAdmissionType() {
        return admissionType;
    }

    public void setAdmissionType(String admissionType) {
        this.admissionType = admissionType;
    }

    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDischargeSummary() {
        return dischargeSummary;
    }

    public void setDischargeSummary(String dischargeSummary) {
        this.dischargeSummary = dischargeSummary;
    }

    public String getDischargeInstructions() {
        return dischargeInstructions;
    }

    public void setDischargeInstructions(String dischargeInstructions) {
        this.dischargeInstructions = dischargeInstructions;
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
        return "Admission{" +
                "admissionId=" + admissionId +
                ", patientId=" + patientId +
                ", roomId=" + roomId +
                ", diagnosis='" + diagnosis + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
