package com.siteworks.rentals.service;

import com.siteworks.rentals.dto.CreateRentalRequest;
import com.siteworks.rentals.entity.*;
import com.siteworks.rentals.repository.EquipmentRepository;
import com.siteworks.rentals.repository.RentalRepository;
import com.siteworks.rentals.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getRentalsByClient(User client) {
        return rentalRepository.findByClientOrderByCreatedAtDesc(client);
    }

    public List<Rental> getRentalsByClientId(Long clientId) {
        return rentalRepository.findByClientId(clientId);
    }

    public List<Rental> getActiveRentals() {
        return rentalRepository.findActiveRentals();
    }

    public List<Rental> getTodaysRentals() {
        return rentalRepository.findTodaysRentals();
    }

    public List<Rental> getUpcomingRentals() {
        return rentalRepository.findUpcomingRentals(LocalDateTime.now());
    }

    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    public Rental createRental(CreateRentalRequest request, User client) {
        // Calculate total cost
        BigDecimal totalCost = calculateTotalCost(request.getEquipmentIds(), request.getStartDate(), request.getEndDate());

        // Create rental
        Rental rental = new Rental();
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setTotalCost(totalCost);
        rental.setNotes(request.getNotes());
        rental.setClient(client);
        rental.setStatus(RentalStatus.RESERVED);

        // Save rental first
        rental = rentalRepository.save(rental);

        // Create rental equipment entries
        List<RentalEquipment> rentalEquipmentList = new ArrayList<>();
        int days = (int) ChronoUnit.DAYS.between(request.getStartDate().toLocalDate(), request.getEndDate().toLocalDate());
        if (days == 0) days = 1; // Minimum 1 day rental

        for (Long equipmentId : request.getEquipmentIds()) {
            Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
            if (equipmentOpt.isPresent()) {
                Equipment equipment = equipmentOpt.get();
                RentalEquipment rentalEquipment = new RentalEquipment();
                rentalEquipment.setRental(rental);
                rentalEquipment.setEquipment(equipment);
                rentalEquipment.setDailyRateAtBooking(equipment.getDailyRate());
                rentalEquipment.setDaysRented(days);
                rentalEquipmentList.add(rentalEquipment);
            }
        }

        rental.setRentalEquipment(rentalEquipmentList);
        return rentalRepository.save(rental);
    }

    public Rental updateRentalStatus(Long rentalId, RentalStatus newStatus) {
        Optional<Rental> optionalRental = rentalRepository.findById(rentalId);
        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            rental.setStatus(newStatus);
            return rentalRepository.save(rental);
        }
        return null;
    }

    public boolean cancelRental(Long rentalId) {
        Optional<Rental> optionalRental = rentalRepository.findById(rentalId);
        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            rental.setStatus(RentalStatus.CANCELLED);
            rentalRepository.save(rental);
            return true;
        }
        return false;
    }

    private BigDecimal calculateTotalCost(List<Long> equipmentIds, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalCost = BigDecimal.ZERO;
        int days = (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        if (days == 0) days = 1; // Minimum 1 day rental

        for (Long equipmentId : equipmentIds) {
            Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
            if (equipmentOpt.isPresent()) {
                Equipment equipment = equipmentOpt.get();
                BigDecimal equipmentCost = equipment.getDailyRate().multiply(BigDecimal.valueOf(days));
                totalCost = totalCost.add(equipmentCost);
            }
        }

        return totalCost;
    }

    public boolean isEquipmentAvailableForRental(List<Long> equipmentIds, LocalDateTime startDate, LocalDateTime endDate) {
        for (Long equipmentId : equipmentIds) {
            Equipment equipment = equipmentRepository.findAvailableEquipmentForDateRange(equipmentId, startDate, endDate);
            if (equipment == null) {
                return false;
            }
        }
        return true;
    }
}