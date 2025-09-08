// Update your UserController.java - add PasswordEncoder as a class field:

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
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Add this field at class level

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> getUserProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findById(userDetails.getId());

        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }

        UserInfoResponse userInfoResponse = userService.convertToUserInfoResponse(user);
        return ResponseEntity.ok(userInfoResponse);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> updateUserProfile(@RequestBody Map<String, String> updates) {
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

        User updatedUser = userService.save(user);
        UserInfoResponse userInfoResponse = userService.convertToUserInfoResponse(updatedUser);

        return ResponseEntity.ok(userInfoResponse);
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findById(userDetails.getId());

            if (user == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }

            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Current password and new password are required"));
            }

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Current password is incorrect"));
            }

            // Validate new password
            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest().body(new MessageResponse("New password must be at least 6 characters long"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.save(user);

            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Failed to change password: " + e.getMessage()));
        }
    }

    @GetMapping("/clients")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> getAllClients() {
        List<User> clients = userService.getAllClients();
        List<UserInfoResponse> clientsResponse = clients.stream()
                .map(userService::convertToUserInfoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientsResponse);
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> getAllStaff() {
        List<User> staff = userService.getAllStaff();
        List<UserInfoResponse> staffResponse = staff.stream()
                .map(userService::convertToUserInfoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(staffResponse);
    }
}