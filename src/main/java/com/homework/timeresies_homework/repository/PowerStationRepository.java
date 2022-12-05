package com.homework.timeresies_homework.repository;

import com.homework.timeresies_homework.entity.PowerStation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PowerStationRepository extends JpaRepository<PowerStation, String> {

    PowerStation findByPowerStationName(String powerStationName);
}
