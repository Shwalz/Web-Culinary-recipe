package com.example.cookbook.repository;

import com.example.cookbook.domain.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

}
