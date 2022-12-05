package com.homework.timeresies_homework.responseData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.homework.timeresies_homework.entity.PowerStation;
import com.homework.timeresies_homework.entity.TimeSeries;
import com.homework.timeresies_homework.entity.TimeSeriesDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseTimeseries {

    @JsonProperty("time-series-id")
    private Long id;
    @JsonProperty("power-station")
    private PowerStation powerStation;
    @JsonProperty("time-series-date")
    private TimeSeriesDate date;
    @JsonProperty("timeseries-series")
    private String series;
    @JsonProperty("version")
    private Integer version;
}
