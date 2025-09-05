package com.siteworks.rentals.dto;

import com.siteworks.rentals.entity.EquipmentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class UpdateEquipmentRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;

    @NotNull
    @Positive
    private BigDecimal dailyRate;

    @NotNull
    private EquipmentCategory category;

    private String specifications;

    private Boolean available;

    private String imageUrl;

    public UpdateEquipmentRequest() {}

    public UpdateEquipmentRequest(String name, String description, BigDecimal dailyRate, EquipmentCategory category,
                                  String specifications, Boolean available, String imageUrl) {
        this.name = name;
        this.description = description;
        this.dailyRate = dailyRate;
        this.category = category;
        this.specifications = specifications;
        this.available = available;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public EquipmentCategory getCategory() {
        return category;
    }

    public void setCategory(EquipmentCategory category) {
        this.category = category;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}