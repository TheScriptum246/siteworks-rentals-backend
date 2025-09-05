package com.siteworks.rentals.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class CreateRentalRequest {
    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private Long clientId; // For staff creating rentals for clients

    @NotNull
    private List<Long> equipmentIds;

    private String notes;

    public CreateRentalRequest() {}

    public CreateRentalRequest(LocalDateTime startDate, LocalDateTime endDate, Long clientId, List<Long> equipmentIds, String notes) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.clientId = clientId;
        this.equipmentIds = equipmentIds;
        this.notes = notes;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}