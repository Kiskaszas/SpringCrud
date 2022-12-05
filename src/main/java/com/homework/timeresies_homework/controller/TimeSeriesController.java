package com.homework.timeresies_homework.controller;

import com.google.gson.Gson;

import com.homework.timeresies_homework.entity.PowerStation;
import com.homework.timeresies_homework.repository.PowerStationRepository;
import com.homework.timeresies_homework.repository.TimeSeriesDateRepository;
import com.homework.timeresies_homework.repository.TimeSeriesRepository;
import com.homework.timeresies_homework.responseData.ResponseTimeseries;
import com.homework.timeresies_homework.entity.TimeSeries;
import com.homework.timeresies_homework.entity.TimeSeriesDate;
import com.homework.timeresies_homework.responseData.error.ResponseError;
import com.homework.timeresies_homework.service.TimeSeriesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@CrossOrigin(origins = {"${spring.cross.origin.link}"})
@RestController
@EnableAsync
@RequestMapping(path = "/api")
public class TimeSeriesController {

    @Autowired
    private TimeSeriesService timeSeriesService;

    @Autowired
    private TimeSeriesRepository timeSeriesRepository;

    @Autowired
    private PowerStationRepository powerStationRepository;

    @Autowired
    private TimeSeriesDateRepository timeSeriesDateRepository;

    private static Logger log = LoggerFactory.getLogger(ResponseError.class);

    @PostMapping("/add/time-series")
    public ResponseEntity<String> createTimeSeries(@RequestBody String timeSeriesJSON) {
        if (!timeSeriesJSON.isEmpty()) {
            TimeSeries timeSeries = setTimeSeriesFromJson(timeSeriesJSON);
            String responseMessage = timeSeriesService.createTimeseries(timeSeries);
            Gson gson = new Gson();

            return ResponseEntity.ok(gson.toJson(responseMessage));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/get/all-time-series", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<? extends Object> getAllTimeSeries() {
        try {
            List<TimeSeries> timeSeriesList = timeSeriesService.getAllTimeSeries();
            List<ResponseTimeseries> responseTimeSeriesData = toResponseTimeSeriesDataList(timeSeriesList);
            if (responseTimeSeriesData.size() < 1) {
                return notFoundInDatabase();
                //return new ResponseEntity<>(responseTimeSeriesData, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(responseTimeSeriesData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseError(1, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/time-series-by-power-station-and-date")
    public ResponseEntity<? extends Object> getTimeSeriesByPowersationAndDate(@RequestParam String powerStationName,
                                                                              @RequestParam String date) {
        try {
            List<TimeSeries> timeSeriesList = timeSeriesService.findTimeSeriesByPowerStationAndDate(powerStationName, date);
            System.out.println(timeSeriesList.toString());
            List<ResponseTimeseries> responseTimeSeriesData = toResponseTimeSeriesDataList(timeSeriesList);
            if (responseTimeSeriesData.size() < 1) {
                return notFoundInDatabase();
            }
            return new ResponseEntity<>(responseTimeSeriesData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseError(1, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/update/time-series")
    public ResponseEntity<? extends Object> updateTimeSeries(@RequestBody String timeSeriesJSON) {
        String updateMessage = null;
        boolean isExist = false;
        if (!timeSeriesJSON.isEmpty()) {
            try {
                TimeSeries newTimeSeries = setTimeSeriesFromJson(timeSeriesJSON);
                updateMessage = timeSeriesService.updateTimeSeries(newTimeSeries);
                return new ResponseEntity<>(updateMessage, HttpStatus.OK);

            } catch (Exception e) {
                return new ResponseEntity<>(new ResponseError(2, e.getMessage()), HttpStatus.BAD_REQUEST);
            }
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping("/delete/time-series/{powerStationName}/{date}/{version}")
    public ResponseEntity<? extends Object> deleteTimeSeries(
            @RequestParam String powerStationName,
            @RequestParam String date,
            @RequestParam Integer version) {

        try {
            PowerStation powerStation = powerStationRepository.findByPowerStationName(powerStationName);
            TimeSeriesDate timeSeriesDate = timeSeriesDateRepository.findByDateAndPowerStationId(date, powerStation.getId());
            TimeSeries timeSeries = timeSeriesService.findByPowerStationAndDateAndVersion(powerStation, timeSeriesDate, version);
            if (null == timeSeries) {
                return notFoundInDatabase();
            }
            if (!powerStationName.isEmpty() && !date.isEmpty() && version == null) {
                String deleteMessage = timeSeriesService.deleteTimeSeries(timeSeries);
                return ResponseEntity.ok(deleteMessage);
            } else {
                return new ResponseEntity<>(new ResponseError(2, "Missing param value"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseError(2, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    private TimeSeries setTimeSeriesFromJson(String timeSeriesJson) {
        Gson gson = new Gson();
        Map<String, Object> timeSeriesMap = gson.fromJson(timeSeriesJson, Map.class);

        TimeSeries timeSeries = new TimeSeries();
        TimeSeriesDate timeSeriesDate = new TimeSeriesDate();
        PowerStation powerStation = new PowerStation();

        timeSeriesDate.setDate((String) timeSeriesMap.get("date"));

        timeSeriesDate.setTimestamp(Timestamp.valueOf((timeSeriesMap.get("timestamp").toString())));

        timeSeriesDate.setZone((String) timeSeriesMap.get("zone"));
        timeSeriesDate.setPeriod((String) timeSeriesMap.get("period"));

        powerStation.setPowerStationName((String) timeSeriesMap.get("power-station"));

        Set<TimeSeriesDate> timeSeriesDates = new HashSet<>();
        timeSeriesDates.add(timeSeriesDate);
        Set<PowerStation> powerStationSet = new HashSet<>();
        for (TimeSeriesDate tsd : timeSeriesDates) {
            powerStationSet.add(powerStation);
            tsd.setPowerStation(powerStationSet);
            powerStationSet.stream().findFirst().get().setTimeSeriesDate(timeSeriesDates);
        }

        timeSeries.setPowerStation(powerStation);
        timeSeries.setDate(timeSeriesDate);
        ArrayList<Double> doubles = (ArrayList<Double>) timeSeriesMap.get("series");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < doubles.size(); i++) {
            double d = doubles.get(i);
            int integer = (int) d;
            if (i < doubles.size() - 1) {
                sb.append(integer).append(",");
            } else {
                sb.append(integer);
            }
        }
        timeSeries.setSeries(sb.toString());
        return timeSeries;
    }

    private List<ResponseTimeseries> toResponseTimeSeriesDataList(List<TimeSeries> timeSeriesList) {
        List<ResponseTimeseries> responseTimeSeriesData = new ArrayList<>();
        for (TimeSeries timeSeries : timeSeriesList) {
            responseTimeSeriesData.add(new ResponseTimeseries(timeSeries.getId(),
                    timeSeries.getPowerStation(),
                    timeSeries.getDate(),
                    timeSeries.getSeries(),
                    timeSeries.getVersion()));
        }
        return responseTimeSeriesData;
    }

    private ResponseEntity<ResponseError> notFoundInDatabase() {
        return new ResponseEntity<ResponseError>(new ResponseError(404, "Not found time series data in database."), HttpStatus.OK);
    }
}
