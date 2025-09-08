package com.siteworks.rentals.controller;

import com.siteworks.rentals.dto.*;
import com.siteworks.rentals.entity.*;
import com.siteworks.rentals.repository.RoleRepository;
import com.siteworks.rentals.repository.UserRepository;
import com.siteworks.rentals.security.jwt.JwtUtils;
import com.siteworks.rentals.security.services.UserDetailsImpl;
import com.siteworks.rentals.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

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

            logger.info("JWT generated successfully, length: {}", jwt != null ? jwt.length() : "NULL");
            logger.info("JWT starts with: {}", jwt != null && jwt.length() > 20 ? jwt.substring(0, 20) + "..." : jwt);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            logger.info("User details - ID: {}, Username: {}, Email: {}",
                    userDetails.getId(), userDetails.getUsername(), userDetails.getEmail());
            logger.info("User roles: {}", roles);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            logger.info("Refresh token created: {}", refreshToken.getToken());

            JwtResponse response = new JwtResponse(jwt,
                    refreshToken.getToken(),
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);

            logger.info("Returning JWT response with token: {}", jwt != null ? "TOKEN_PRESENT" : "TOKEN_NULL");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Authentication failed for username: {}", loginRequest.getUsername(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid username or password!"));
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

        Set<Role> roles = new HashSet<>();

        // Default role is CLIENT
        Role clientRole = roleRepository.findByName(ERole.ROLE_CLIENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(clientRole);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));

            refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            User user = refreshToken.getUser();

            UserDetailsImpl userDetails = UserDetailsImpl.build(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String token = jwtUtils.generateJwtToken(authentication);

            return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Refresh token is not in database!"));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
            return ResponseEntity.ok(new MessageResponse("Log out successful!"));
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("Log out successful!"));
        }
    }

    // Test endpoint to verify the controller is working
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(new MessageResponse("Auth controller is working!"));
    }

    // Debug endpoint to test JWT generation
    @PostMapping("/debug-jwt")
    public ResponseEntity<?> debugJwt(@RequestBody LoginRequest loginRequest) {
        try {
            // Check if user exists
            var userOpt = userRepository.findByUsername(loginRequest.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }

            User user = userOpt.get();
            logger.info("User found: {} with roles: {}", user.getUsername(), user.getRoles());

            // Check password
            boolean passwordMatches = encoder.matches(loginRequest.getPassword(), user.getPassword());
            logger.info("Password matches: {}", passwordMatches);

            if (!passwordMatches) {
                return ResponseEntity.badRequest().body(new MessageResponse("Password incorrect"));
            }

            // Try authentication
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Generate JWT
            String jwt = jwtUtils.generateJwtToken(auth);
            logger.info("Generated JWT: {}", jwt);

            return ResponseEntity.ok(new MessageResponse("JWT: " + jwt));

        } catch (Exception e) {
            logger.error("Debug JWT failed", e);
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}