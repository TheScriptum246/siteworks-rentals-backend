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
    private PasswordEncoder passwordEncoder;

    // Get all users endpoint
    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            List<UserInfoResponse> usersResponse = users.stream()
                    .map(userService::convertToUserInfoResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(usersResponse);
        } catch (Exception e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> getUserProfile() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
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
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            User user = userService.findById(userDetails.getId());

            if (user == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }

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

    @PutMapping("/change-password")
    @PreAuthorize("hasRole('CLIENT') or hasRole('STAFF')")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            User user = userService.findById(userDetails.getId());

            if (user == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }

            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Current password and new password are required"));
            }

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Current password is incorrect"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.save(user);

            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
        } catch (Exception e) {
            System.err.println("Error in changePassword: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }

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

            UserInfoResponse updatedUser = userService.convertToUserInfoResponse(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.err.println("Error in updateUserRole: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Internal server error"));
        }
    }
}