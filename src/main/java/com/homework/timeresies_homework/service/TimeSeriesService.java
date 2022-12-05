package com.homework.timeresies_homework.service;

import com.homework.timeresies_homework.entity.PowerStation;
import com.homework.timeresies_homework.entity.TimeSeries;
import com.homework.timeresies_homework.entity.TimeSeriesDate;
import com.homework.timeresies_homework.repository.PowerStationRepository;
import com.homework.timeresies_homework.repository.TimeSeriesDateRepository;
import com.homework.timeresies_homework.repository.TimeSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TimeSeriesService {

    @Autowired
    private PowerStationRepository powerStationRepository;

    @Autowired
    private TimeSeriesDateRepository timeSeriesDateRepository;

    @Autowired
    private TimeSeriesRepository timeSeriesRepository;

    @Transactional
    public String createTimeseries(TimeSeries timeSeries) {
        try {
            if (null != timeSeries) {
                PowerStation powerStation = powerStationRepository.findByPowerStationName(timeSeries.getPowerStation().getPowerStationName());

                if (null == powerStation && null == timeSeries.getDate().getId()) {
                    timeSeries.setVersion(1);
                    powerStationRepository.save(timeSeries.getPowerStation());
                    timeSeriesDateRepository.save(timeSeries.getDate());
                    timeSeriesRepository.save(timeSeries);
                    return "Time series record created successfully.";
                } else {
                    updateTimeSeries(timeSeries);
                    return "Time series for date already exists in the database. It is created with new version";
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return "Time series creattion failed";
    }

    public List<TimeSeries> getAllTimeSeries() {
        return timeSeriesRepository.findAll();
    }

    public List<TimeSeries> findTimeSeriesByPowerStationAndDate(String powerStation, String date) {
        return timeSeriesRepository.findByPowerStationAndDate(powerStation, date);
    }

    public TimeSeries findByPowerStationAndDateAndVersion(PowerStation powerStation, TimeSeriesDate date, Integer version) {
        return timeSeriesRepository.findByPowerStationAndDateAndVersion(powerStation, date, version);
    }

    @Transactional
    public String updateTimeSeries(TimeSeries newTimeSeries) {
        if (null != newTimeSeries) {
            try {
                PowerStation powerStation = powerStationRepository.findByPowerStationName(newTimeSeries.getPowerStation().getPowerStationName());
                TimeSeriesDate timeSeriesDate = timeSeriesDateRepository.findByDateAndPowerStationId(newTimeSeries.getDate().getDate(), powerStation.getId());
                TimeSeries lastActualTimeseriesVersionUpdate = timeSeriesRepository.findLastTimeSeriesVersionByPowerStationAndDate(powerStation, timeSeriesDate);
                lastActualTimeseriesVersionUpdate.setSeries(newTimeSeries.getSeries());
                lastActualTimeseriesVersionUpdate.getDate().setTimestamp(newTimeSeries.getDate().getTimestamp());
                lastActualTimeseriesVersionUpdate.getDate().setPeriod(newTimeSeries.getDate().getPeriod());
                lastActualTimeseriesVersionUpdate.setVersion(lastActualTimeseriesVersionUpdate.getVersion() + 1);
                timeSeriesRepository.save(lastActualTimeseriesVersionUpdate);
                return "Time Series new verson inserted.";
            } catch (Exception e) {
                throw e;
            }
        } else {
            return "Time Series is exists in the database.";
        }
    }

    @Transactional
    public String deleteTimeSeries(TimeSeries timeSeries) {
        PowerStation powerStation = powerStationRepository.findByPowerStationName(timeSeries.getPowerStation().getPowerStationName());
        if (null == powerStation) {
            timeSeriesDateRepository.delete(timeSeriesDateRepository.findByDateAndPowerStationId(timeSeries.getDate().getDate(), powerStation.getId()));
            powerStationRepository.delete(powerStation);
            timeSeriesRepository.delete(timeSeries);
            return "Time series record deleted successfully.";
        } else {
            return "Time series does not exist";
        }
    }

    @Transactional
    public boolean existsByPowerStationAndDate(Long powerStation, Long timeSeriesDate) {
        return timeSeriesRepository.existsByPowerStationAndDate(powerStation, timeSeriesDate);
    }

    public void deleteAll() {
        timeSeriesRepository.deleteAll();
        powerStationRepository.deleteAll();
        timeSeriesDateRepository.deleteAll();
    }
}
