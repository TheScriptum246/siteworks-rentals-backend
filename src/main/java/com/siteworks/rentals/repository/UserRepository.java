package com.siteworks.rentals.repository;

import com.siteworks.rentals.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") User.Role role);

    // Find all clients
    @Query("SELECT u FROM User u WHERE u.role = 'CLIENT'")
    List<User> findAllClients();

    // Find all staff
    @Query("SELECT u FROM User u WHERE u.role = 'STAFF'")
    List<User> findAllStaff();
}