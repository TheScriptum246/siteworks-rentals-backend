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

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/rentals") // ✅ FIXED: Removed /api since it's in context-path
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getAllRentals() {
        try {
            System.out.println("getAllRentals called"); // Debug log
            List<Rental> rentals = rentalService.getAllRentals();
            System.out.println("Found " + (rentals != null ? rentals.size() : 0) + " rentals"); // Debug log
            return ResponseEntity.ok(rentals != null ? rentals : Collections.emptyList());
        } catch (Exception e) {
            System.err.println("Error in getAllRentals: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getMyRentals() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findById(userDetails.getId());

            if (user == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Rental> rentals = rentalService.getRentalsByClient(user);
            return ResponseEntity.ok(rentals != null ? rentals : Collections.emptyList());
        } catch (Exception e) {
            System.err.println("Error in getMyRentals: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getRentalsByClientId(@PathVariable Long clientId) {
        try {
            List<Rental> rentals = rentalService.getRentalsByClientId(clientId);
            return ResponseEntity.ok(rentals != null ? rentals : Collections.emptyList());
        } catch (Exception e) {
            System.err.println("Error in getRentalsByClientId: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getActiveRentals() {
        try {
            List<Rental> rentals = rentalService.getActiveRentals();
            return ResponseEntity.ok(rentals != null ? rentals : Collections.emptyList());
        } catch (Exception e) {
            System.err.println("Error in getActiveRentals: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/today")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getTodaysRentals() {
        try {
            List<Rental> rentals = rentalService.getTodaysRentals();
            return ResponseEntity.ok(rentals != null ? rentals : Collections.emptyList());
        } catch (Exception e) {
            System.err.println("Error in getTodaysRentals: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Rental>> getUpcomingRentals() {
        try {
            List<Rental> rentals = rentalService.getUpcomingRentals();
            return ResponseEntity.ok(rentals != null ? rentals : Collections.emptyList());
        } catch (Exception e) {
            System.err.println("Error in getUpcomingRentals: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> getRentalById(@PathVariable Long id) {
        try {
            Rental rental = rentalService.getRentalById(id);
            if (rental == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Rental not found"));
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = userService.findById(userDetails.getId());

            // Staff can view any rental, clients can only view their own
            if (currentUser.getRole() == User.Role.STAFF ||
                    rental.getClient().getId().equals(currentUser.getId())) {
                return ResponseEntity.ok(rental);
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Access denied"));
            }
        } catch (Exception e) {
            System.err.println("Error in getRentalById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> createRental(@Valid @RequestBody CreateRentalRequest request) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = userService.findById(userDetails.getId());

            User client;
            if (request.getClientId() != null && currentUser.getRole() == User.Role.STAFF) {
                client = userService.findById(request.getClientId());
                if (client == null) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Client not found"));
                }
            } else {
                client = currentUser;
            }

            // Check equipment availability
            if (!rentalService.isEquipmentAvailableForRental(request.getEquipmentIds(), request.getStartDate(), request.getEndDate())) {
                return ResponseEntity.badRequest().body(new MessageResponse("One or more equipment items are not available for the selected dates"));
            }

            Rental rental = rentalService.createRental(request, client);
            return ResponseEntity.ok(rental);
        } catch (Exception e) {
            System.err.println("Error in createRental: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> updateRentalStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
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
        } catch (Exception e) {
            System.err.println("Error in updateRentalStatus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> cancelRental(@PathVariable Long id) {
        try {
            Rental rental = rentalService.getRentalById(id);
            if (rental == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Rental not found"));
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = userService.findById(userDetails.getId());

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
        } catch (Exception e) {
            System.err.println("Error in cancelRental: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }
}