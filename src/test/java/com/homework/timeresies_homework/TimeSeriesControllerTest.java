package com.homework.timeresies_homework;
import com.homework.timeresies_homework.entity.PowerStation;
import com.homework.timeresies_homework.entity.TimeSeries;
import com.homework.timeresies_homework.entity.TimeSeriesDate;
import com.homework.timeresies_homework.responseData.error.ResponseError;
import com.homework.timeresies_homework.service.TimeSeriesService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TimeSeriesControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TimeSeriesService timeSeriesService;

    private List<File> files;
    private List<String> timeSeriesJsonStringList;
    private String sampleDataDirectory = "./sample_data";

    private TimeSeries testTimeSeries;

    private HashMap<String, Object> timeSeriesJsonMap = new HashMap<String, Object>() {{
        put("power-station", "Naperőmű 2021 Kft. Iborfia");
        put("date", "2021-06-28");
        put("zone", "Europe/Budapes");
        put("period", "PT15M");
        put("timestamp", "2021-06-28 04:30:09");
        put("series", "0,0,0,0,0,0,0,0,0,123,1234,12345,123456,1234567,12345678,123456789,87654321,7654321,654321,54321,4321,321,21,1,0,0,0,0");
    }};

    @BeforeEach
    public void initialiseRestAssuredMockMvcWebApplicationContext() {
        timeSeriesJsonStringList = new ArrayList<>();
        RestAssured.port = port;
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        timeSeriesJsonMap = new  HashMap<String, Object>();
        TimeSeries timeSeries = new TimeSeries();
        TimeSeriesDate timeSeriesDate = new TimeSeriesDate();
        PowerStation powerStation = new PowerStation();

        timeSeriesDate.setDate("2021-06-28");
        timeSeriesDate.setTimestamp(Timestamp.valueOf("2021-06-28 04:30:09"));
        timeSeriesDate.setZone("Europe/Budapes");
        timeSeriesDate.setPeriod("PT15M");

        powerStation.setPowerStationName("Naperőmű 2021 Kft. Iborfia");

        Set<TimeSeriesDate> timeSeriesDates = new HashSet<>();
        timeSeriesDates.add(timeSeriesDate);
        Set<PowerStation> powerStationSet = new HashSet<>();
        for (TimeSeriesDate tsd: timeSeriesDates) {
            powerStationSet.add(powerStation);
            tsd.setPowerStation(powerStationSet);
            powerStationSet.stream().findFirst().get().setTimeSeriesDate(timeSeriesDates);
        }
        timeSeries.setPowerStation(powerStation);
        timeSeries.setDate(timeSeriesDate);

        timeSeriesJsonMap.put("power-station", powerStation.getPowerStationName());
        timeSeriesJsonMap.put("date", timeSeriesDate.getDate());
        timeSeriesJsonMap.put("zone", timeSeriesDate.getZone());
        timeSeriesJsonMap.put("period", timeSeriesDate.getPeriod());
        timeSeriesJsonMap.put("timestamp", "2021-06-28 04:30:09");
        ArrayList<Double> doubles = new ArrayList<>();
        doubles.addAll(
                Arrays.asList(0.0,123.0,1234.0,12345.0,123456.0,1234567.0,321.0,21.0,1.0,0.0,0.0,0.0,0.0)
        );
        timeSeriesJsonMap.put("series", doubles);

    }

    //@AfterEach
    public void clearDatabase() {
        timeSeriesService.deleteAll();
    }

    @Test
    @Order(1)
    public void addTimeSeries() {
        given()
               .contentType(ContentType.JSON)
                .body(timeSeriesJsonMap)
                .when().post("/api/add/time-series").then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo("\"Time series record created successfully.\""));
    }

    @Test
    @Order(2)
    public void updateTimeSeries() {

        timeSeriesJsonMap.put("period", "PT20M");
        given().
                contentType(ContentType.JSON)
                .body(timeSeriesJsonMap)
                .when().put("/api/update/time-series").then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo("Time Series new verson inserted."));
    }

    @Test
    @Order(3)
    public void getAllTimeSeries(){
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/get/all-time-series")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.any(String.class));
    }


    @Test
    @Order(4)
    public void getTimeSeriesByPowersationAndDate(){
        ResponseError responseerror = new ResponseError(404, "Not found time series data in database.");
        given()
                .contentType(ContentType.JSON)
                //.body(map)
                .when()
                .get("/api/get/time-series-by-power-station-and-date?powerStationName=Solar-Power 2032 Ltd. Iborfia&date=2032-06-28")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo("{\"errorCode\":404,\"errorMessage\":\"Not found time series data in database.\"}"));
    }
}
