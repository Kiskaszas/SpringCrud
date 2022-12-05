package com.homework.timeresies_homework.repository;

import com.homework.timeresies_homework.entity.TimeSeriesDate;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TimeSeriesDateRepository extends JpaRepository<TimeSeriesDate, Long> {

    TimeSeriesDate findByDateAndPowerStationId(String date, Long powerStationId);
}
