package com.siteworks.rentals.controller;

import com.siteworks.rentals.dto.*;
import com.siteworks.rentals.entity.*;
import com.siteworks.rentals.repository.UserRepository;
import com.siteworks.rentals.security.jwt.JwtUtils;
import com.siteworks.rentals.security.services.UserDetailsImpl;
import com.siteworks.rentals.security.services.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for username: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);

            logger.info("JWT generated successfully");

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            logger.info("User authenticated - ID: {}, Username: {}",
                    userDetails.getId(), userDetails.getUsername());

            JwtResponse response = new JwtResponse(jwt,
                    refreshToken,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Authentication failed for username: {}", loginRequest.getUsername(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid username or password!"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // Validate refresh token
            if (!jwtUtils.validateJwtToken(refreshToken)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid or expired refresh token"));
            }

            // Extract username from refresh token
            String username = jwtUtils.getUserNameFromJwtToken(refreshToken);

            // Load user details to ensure user still exists and is active
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Create new authentication
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // Generate new tokens
            String newAccessToken = jwtUtils.generateJwtToken(authentication);
            String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

            UserDetailsImpl userPrincipal = (UserDetailsImpl) userDetails;
            List<String> roles = userPrincipal.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(
                    newAccessToken,
                    newRefreshToken,
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getEmail(),
                    roles
            ));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("User not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to refresh token: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName());

        user.setPhone(signUpRequest.getPhone());
        // Default role is CLIENT (set in User constructor)

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        // With JWT-based refresh tokens, we don't need to clean up database
        // Client should remove tokens from storage
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}