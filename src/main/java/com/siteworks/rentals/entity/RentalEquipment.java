package com.siteworks.rentals.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rental_equipment")
public class RentalEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    @JsonBackReference
    private Rental rental;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(name = "daily_rate_at_booking", precision = 10, scale = 2)
    private BigDecimal dailyRateAtBooking;

    @Column(name = "days_rented")
    private Integer daysRented;

    public RentalEquipment() {}

    public RentalEquipment(Rental rental, Equipment equipment, BigDecimal dailyRateAtBooking, Integer daysRented) {
        this.rental = rental;
        this.equipment = equipment;
        this.dailyRateAtBooking = dailyRateAtBooking;
        this.daysRented = daysRented;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Rental getRental() { return rental; }
    public void setRental(Rental rental) { this.rental = rental; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public BigDecimal getDailyRateAtBooking() { return dailyRateAtBooking; }
    public void setDailyRateAtBooking(BigDecimal dailyRateAtBooking) { this.dailyRateAtBooking = dailyRateAtBooking; }

    public Integer getDaysRented() { return daysRented; }
    public void setDaysRented(Integer daysRented) { this.daysRented = daysRented; }
}