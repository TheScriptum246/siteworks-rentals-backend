package com.siteworks.rentals.controller;

import com.siteworks.rentals.dto.CreateRentalRequest;
import com.siteworks.rentals.dto.MessageResponse;
import com.siteworks.rentals.entity.Rental;
import com.siteworks.rentals.entity.RentalStatus;
import com.siteworks.rentals.entity.User;
import com.siteworks.rentals.security.services.UserDetailsImpl;
import com.siteworks.rentals.service.RentalService;
import com.siteworks.rentals.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getAllRentals() {
        List<Rental> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getMyRentals() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findById(userDetails.getId());
        List<Rental> rentals = rentalService.getRentalsByClient(user);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getRentalsByClientId(@PathVariable Long clientId) {
        List<Rental> rentals = rentalService.getRentalsByClientId(clientId);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getActiveRentals() {
        List<Rental> rentals = rentalService.getActiveRentals();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/today")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getTodaysRentals() {
        List<Rental> rentals = rentalService.getTodaysRentals();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getUpcomingRentals() {
        List<Rental> rentals = rentalService.getUpcomingRentals();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> getRentalById(@PathVariable Long id) {
        Rental rental = rentalService.getRentalById(id);
        if (rental == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Rental not found"));
        }

        // Check if client is accessing their own rental
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findById(userDetails.getId());

        // Staff can view any rental, clients can only view their own
        // Updated to use simplified role checking
        if (currentUser.getRole() == User.Role.STAFF ||
                rental.getClient().getId().equals(currentUser.getId())) {
            return ResponseEntity.ok(rental);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied"));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> createRental(@Valid @RequestBody CreateRentalRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findById(userDetails.getId());

        User client;

        // If request has clientId and current user is staff, use that client
        // Updated to use simplified role checking
        if (request.getClientId() != null && currentUser.getRole() == User.Role.STAFF) {
            client = userService.findById(request.getClientId());
            if (client == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Client not found"));
            }
        } else {
            // Otherwise, use current user as client
            client = currentUser;
        }

        // Check equipment availability
        if (!rentalService.isEquipmentAvailableForRental(request.getEquipmentIds(), request.getStartDate(), request.getEndDate())) {
            return ResponseEntity.badRequest().body(new MessageResponse("One or more equipment items are not available for the selected dates"));
        }

        Rental rental = rentalService.createRental(request, client);
        return ResponseEntity.ok(rental);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> updateRentalStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        String newStatusStr = statusUpdate.get("status");

        if (newStatusStr == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Status is required"));
        }

        RentalStatus newStatus;
        try {
            newStatus = RentalStatus.valueOf(newStatusStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid status"));
        }

        Rental rental = rentalService.updateRentalStatus(id, newStatus);
        if (rental == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Rental not found"));
        }

        return ResponseEntity.ok(rental);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> cancelRental(@PathVariable Long id) {
        Rental rental = rentalService.getRentalById(id);
        if (rental == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Rental not found"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findById(userDetails.getId());

        // Check if client is canceling their own rental or if staff is canceling
        // Updated to use simplified role checking
        if (currentUser.getRole() == User.Role.STAFF ||
                rental.getClient().getId().equals(currentUser.getId())) {

            boolean cancelled = rentalService.cancelRental(id);
            if (!cancelled) {
                return ResponseEntity.badRequest().body(new MessageResponse("Failed to cancel rental"));
            }

            return ResponseEntity.ok(new MessageResponse("Rental cancelled successfully"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied"));
        }
    }
}