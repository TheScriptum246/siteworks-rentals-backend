package com.siteworks.rentals.controller;

import com.siteworks.rentals.dto.MessageResponse;
import com.siteworks.rentals.dto.UserInfoResponse;
import com.siteworks.rentals.entity.User;
import com.siteworks.rentals.security.services.UserDetailsImpl;
import com.siteworks.rentals.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users") // ✅ FIXED: Added /api prefix to match frontend calls
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ ADDED: Get all users endpoint that was missing
    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers(); // Make sure this method exists in UserService
            List<UserInfoResponse> usersResponse = users.stream()
                    .map(userService::convertToUserInfoResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(usersResponse);
        } catch (Exception e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ Test endpoint to verify controller is working
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("SiteWorks UserController is working! 🎉");
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> getUserProfile() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findById(userDetails.getId());

            if (user == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }

            UserInfoResponse userInfoResponse = userService.convertToUserInfoResponse(user);
            return ResponseEntity.ok(userInfoResponse);
        } catch (Exception e) {
            System.err.println("Error in getUserProfile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> updateUserProfile(@RequestBody Map<String, String> updates) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findById(userDetails.getId());

            if (user == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }

            // Update allowed fields
            if (updates.containsKey("firstName")) {
                user.setFirstName(updates.get("firstName"));
            }
            if (updates.containsKey("lastName")) {
                user.setLastName(updates.get("lastName"));
            }
            if (updates.containsKey("phone")) {
                user.setPhone(updates.get("phone"));
            }
            if (updates.containsKey("email")) {
                if (!updates.get("email").equals(user.getEmail()) &&
                        userService.existsByEmail(updates.get("email"))) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Email already in use"));
                }
                user.setEmail(updates.get("email"));
            }

            userService.save(user);
            UserInfoResponse updatedUser = userService.convertToUserInfoResponse(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.err.println("Error in updateUserProfile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }

    // ✅ ADDED: Update user role endpoint for client management
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> roleUpdate) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }

            String newRoleStr = roleUpdate.get("role");
            if (newRoleStr == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Role is required"));
            }

            User.Role newRole;
            try {
                newRole = User.Role.valueOf(newRoleStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid role"));
            }

            user.setRole(newRole);
            userService.save(user);

            return ResponseEntity.ok(new MessageResponse("User role updated successfully"));

        } catch (Exception e) {
            System.err.println("Error in updateUserRole: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }

    @GetMapping("/clients")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> getAllClients() {
        try {
            List<User> clients = userService.getAllClients();
            List<UserInfoResponse> clientsResponse = clients.stream()
                    .map(userService::convertToUserInfoResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(clientsResponse);
        } catch (Exception e) {
            System.err.println("Error in getAllClients: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> getAllStaff() {
        try {
            List<User> staff = userService.getAllStaff();
            List<UserInfoResponse> staffResponse = staff.stream()
                    .map(userService::convertToUserInfoResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(staffResponse);
        } catch (Exception e) {
            System.err.println("Error in getAllStaff: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}