package com.homework.timeresies_homework.repository;

import com.homework.timeresies_homework.entity.PowerStation;
import com.homework.timeresies_homework.entity.TimeSeries;
import com.homework.timeresies_homework.entity.TimeSeriesDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSeriesRepository extends JpaRepository<TimeSeries, Integer> {

    @Query("select ts from TimeSeries ts where ts.powerStation.powerStationName=?1 and ts.date.date=?2")
    List<TimeSeries> findByPowerStationAndDate(String powerStation, String date);

    @Query("select ts from TimeSeries ts where ts.powerStation=?1 AND ts.date=?2 ORDER BY ts.version DESC nulls first ")
    TimeSeries findLastTimeSeriesVersionByPowerStationAndDate(PowerStation powerStation, TimeSeriesDate timeSeriesDate);

    boolean existsByPowerStationAndDate(Long powerStation, Long timeSeriesDate);

    @Query("select ts from TimeSeries ts where ts.powerStation=?1 AND ts.date=?2 AND ts.version=?3")
    TimeSeries findByPowerStationAndDateAndVersion(PowerStation powerStation, TimeSeriesDate date, Integer version);

    //@Query("delete from TimeSeries ts where ts.powerStation=?1 and ts.date=?2 and ts.version=?3")
    void deleteTimeSeriesByPowerStationAndDateAndVersion(PowerStation powerStation, TimeSeriesDate date, Integer version);

    @Query("select ts from TimeSeries ts where ts.powerStation=?1 AND ts.date=?2 AND ts.version=?3")
    boolean existsByPowerStationAndDate(Long powerStationId, Long timeSeriesDateId, int version);
}
