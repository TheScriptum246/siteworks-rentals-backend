package com.siteworks.rentals.repository;

import com.siteworks.rentals.entity.Rental;
import com.siteworks.rentals.entity.RentalStatus;
import com.siteworks.rentals.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // Find rentals by client
    List<Rental> findByClient(User client);

    // Find rentals by client ID
    List<Rental> findByClientId(Long clientId);

    // Find rentals by status
    List<Rental> findByStatus(RentalStatus status);

    // Find rentals by client and status
    List<Rental> findByClientAndStatus(User client, RentalStatus status);

    // Find rentals within date range
    @Query("SELECT r FROM Rental r WHERE r.startDate >= :startDate AND r.endDate <= :endDate")
    List<Rental> findRentalsInDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    // Find active rentals (RESERVED, CONFIRMED, ACTIVE)
    @Query("SELECT r FROM Rental r WHERE r.status IN ('RESERVED', 'CONFIRMED', 'ACTIVE')")
    List<Rental> findActiveRentals();

    // Find rentals for today
    @Query("SELECT r FROM Rental r WHERE DATE(r.startDate) = CURRENT_DATE OR DATE(r.endDate) = CURRENT_DATE")
    List<Rental> findTodaysRentals();

    // Find upcoming rentals
    @Query("SELECT r FROM Rental r WHERE r.startDate > :currentDate AND r.status IN ('RESERVED', 'CONFIRMED')")
    List<Rental> findUpcomingRentals(@Param("currentDate") LocalDateTime currentDate);

    // Find rentals by equipment
    @Query("SELECT r FROM Rental r JOIN r.rentalEquipment re WHERE re.equipment.id = :equipmentId")
    List<Rental> findByEquipmentId(@Param("equipmentId") Long equipmentId);

    // Find client's rental history (ordered by date)
    @Query("SELECT r FROM Rental r WHERE r.client = :client ORDER BY r.createdAt DESC")
    List<Rental> findByClientOrderByCreatedAtDesc(@Param("client") User client);
}