package com.siteworks.rentals.controller;

import com.siteworks.rentals.dto.MessageResponse;
import com.siteworks.rentals.dto.UpdateEquipmentRequest;
import com.siteworks.rentals.entity.Equipment;
import com.siteworks.rentals.entity.EquipmentCategory;
import com.siteworks.rentals.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<List<Equipment>> getAllAvailableEquipment() {
        List<Equipment> equipment = equipmentService.getAllAvailableEquipment();
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/categories")
    public ResponseEntity<EquipmentCategory[]> getEquipmentCategories() {
        return ResponseEntity.ok(EquipmentCategory.values());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        List<Equipment> equipment = equipmentService.getAllEquipment();
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEquipmentById(@PathVariable Long id) {
        Equipment equipment = equipmentService.getEquipmentById(id);
        if (equipment == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Equipment not found"));
        }
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Equipment>> getEquipmentByCategory(@PathVariable EquipmentCategory category) {
        List<Equipment> equipment = equipmentService.getAvailableEquipmentByCategory(category);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Equipment>> searchEquipment(@RequestParam String name) {
        List<Equipment> equipment = equipmentService.searchEquipmentByName(name);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Equipment>> getAvailableEquipmentForDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Equipment> equipment = equipmentService.getAvailableEquipmentForDateRange(startDate, endDate);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<?> checkEquipmentAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        boolean available = equipmentService.isEquipmentAvailableForDateRange(id, startDate, endDate);
        return ResponseEntity.ok(new MessageResponse(available ? "Equipment is available" : "Equipment is not available"));
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> createEquipment(@Valid @RequestBody UpdateEquipmentRequest request) {
        Equipment equipment = equipmentService.createEquipment(request);
        return ResponseEntity.ok(equipment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> updateEquipment(@PathVariable Long id, @Valid @RequestBody UpdateEquipmentRequest request) {
        Equipment equipment = equipmentService.updateEquipment(id, request);
        if (equipment == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Equipment not found"));
        }
        return ResponseEntity.ok(equipment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> deleteEquipment(@PathVariable Long id) {
        boolean deleted = equipmentService.deleteEquipment(id);
        if (!deleted) {
            return ResponseEntity.badRequest().body(new MessageResponse("Equipment not found"));
        }
        return ResponseEntity.ok(new MessageResponse("Equipment deactivated successfully"));
    }
}