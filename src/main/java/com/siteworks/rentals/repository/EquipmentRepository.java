package com.siteworks.rentals.repository;

import com.siteworks.rentals.entity.Equipment;
import com.siteworks.rentals.entity.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    // Find all available equipment
    List<Equipment> findByAvailableTrue();

    // Find equipment by category
    List<Equipment> findByCategory(EquipmentCategory category);

    // Find available equipment by category
    List<Equipment> findByCategoryAndAvailableTrue(EquipmentCategory category);

    // Find equipment by name containing (search)
    List<Equipment> findByNameContainingIgnoreCaseAndAvailableTrue(String name);

    // Check equipment availability for specific date range
    @Query("SELECT e FROM Equipment e WHERE e.id = :equipmentId " +
            "AND e.available = true " +
            "AND e.id NOT IN (" +
            "    SELECT re.equipment.id FROM RentalEquipment re " +
            "    JOIN re.rental r " +
            "    WHERE r.status IN ('RESERVED', 'CONFIRMED', 'ACTIVE') " +
            "    AND NOT (r.endDate <= :startDate OR r.startDate >= :endDate)" +
            ")")
    Equipment findAvailableEquipmentForDateRange(@Param("equipmentId") Long equipmentId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    // Find all available equipment for specific date range
    @Query("SELECT e FROM Equipment e WHERE e.available = true " +
            "AND e.id NOT IN (" +
            "    SELECT re.equipment.id FROM RentalEquipment re " +
            "    JOIN re.rental r " +
            "    WHERE r.status IN ('RESERVED', 'CONFIRMED', 'ACTIVE') " +
            "    AND NOT (r.endDate <= :startDate OR r.startDate >= :endDate)" +
            ")")
    List<Equipment> findAllAvailableForDateRange(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
}