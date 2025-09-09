package com.siteworks.rentals.service;

import com.siteworks.rentals.dto.UserInfoResponse;
import com.siteworks.rentals.entity.User;
import com.siteworks.rentals.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ✅ ADDED: Get all users method that was missing
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<User> getAllClients() {
        try {
            return userRepository.findAllClients();
        } catch (Exception e) {
            System.err.println("Error in getAllClients: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<User> getAllStaff() {
        try {
            return userRepository.findAllStaff();
        } catch (Exception e) {
            System.err.println("Error in getAllStaff: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public User findById(Long id) {
        try {
            return userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error in findById: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public User findByUsername(String username) {
        try {
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            System.err.println("Error in findByUsername: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public User findByEmail(String email) {
        try {
            return userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            System.err.println("Error in findByEmail: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean existsByUsername(String username) {
        try {
            return userRepository.existsByUsername(username);
        } catch (Exception e) {
            System.err.println("Error in existsByUsername: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByEmail(String email) {
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            System.err.println("Error in existsByEmail: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Error in save: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public UserInfoResponse convertToUserInfoResponse(User user) {
        try {
            List<String> roles = Collections.singletonList("ROLE_" + user.getRole().name());

            return new UserInfoResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhone(),
                    roles,
                    user.getCreatedAt()
            );
        } catch (Exception e) {
            System.err.println("Error in convertToUserInfoResponse: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}