package com.siteworks.rentals.repository;

import com.siteworks.rentals.entity.ERole;
import com.siteworks.rentals.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}