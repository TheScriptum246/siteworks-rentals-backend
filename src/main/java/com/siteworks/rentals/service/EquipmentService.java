package com.siteworks.rentals.service;

import com.siteworks.rentals.dto.UpdateEquipmentRequest;
import com.siteworks.rentals.entity.Equipment;
import com.siteworks.rentals.entity.EquipmentCategory;
import com.siteworks.rentals.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> getAllAvailableEquipment() {
        return equipmentRepository.findByAvailableTrue();
    }

    public List<Equipment> getEquipmentByCategory(EquipmentCategory category) {
        return equipmentRepository.findByCategory(category);
    }

    public List<Equipment> getAvailableEquipmentByCategory(EquipmentCategory category) {
        return equipmentRepository.findByCategoryAndAvailableTrue(category);
    }

    public List<Equipment> searchEquipmentByName(String name) {
        return equipmentRepository.findByNameContainingIgnoreCaseAndAvailableTrue(name);
    }

    public List<Equipment> getAvailableEquipmentForDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return equipmentRepository.findAllAvailableForDateRange(startDate, endDate);
    }

    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id).orElse(null);
    }

    public boolean isEquipmentAvailableForDateRange(Long equipmentId, LocalDateTime startDate, LocalDateTime endDate) {
        Equipment equipment = equipmentRepository.findAvailableEquipmentForDateRange(equipmentId, startDate, endDate);
        return equipment != null;
    }

    public Equipment createEquipment(UpdateEquipmentRequest request) {
        Equipment equipment = new Equipment();
        equipment.setName(request.getName());
        equipment.setDescription(request.getDescription());
        equipment.setDailyRate(request.getDailyRate());
        equipment.setCategory(request.getCategory());
        equipment.setSpecifications(request.getSpecifications());
        equipment.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        equipment.setImageUrl(request.getImageUrl());

        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(Long id, UpdateEquipmentRequest request) {
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);
        if (optionalEquipment.isPresent()) {
            Equipment equipment = optionalEquipment.get();
            equipment.setName(request.getName());
            equipment.setDescription(request.getDescription());
            equipment.setDailyRate(request.getDailyRate());
            equipment.setCategory(request.getCategory());
            equipment.setSpecifications(request.getSpecifications());
            if (request.getAvailable() != null) {
                equipment.setAvailable(request.getAvailable());
            }
            equipment.setImageUrl(request.getImageUrl());

            return equipmentRepository.save(equipment);
        }
        return null;
    }

    public boolean deleteEquipment(Long id) {
        Optional<Equipment> equipment = equipmentRepository.findById(id);
        if (equipment.isPresent()) {
            // Instead of hard delete, mark as unavailable
            Equipment eq = equipment.get();
            eq.setAvailable(false);
            equipmentRepository.save(eq);
            return true;
        }
        return false;
    }
}