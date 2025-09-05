package com.siteworks.rentals.repository;

import com.siteworks.rentals.entity.User;
import com.siteworks.rentals.entity.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
    List<User> findByRole(ERole role);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_CLIENT'")
    List<User> findAllClients();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_STAFF'")
    List<User> findAllStaff();
}