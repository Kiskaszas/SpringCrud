package com.homework.timeresies_homework.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TimeSeries {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "time_series_generator")
    @SequenceGenerator(name="time_series_generator", sequenceName = "time_series_seq")
    @Column(updatable = false, nullable = false)
    private Long id;

    @JsonIgnore
    @OneToOne(targetEntity = PowerStation.class, cascade = CascadeType.PERSIST)
    private PowerStation powerStation;

    @JsonIgnore
    @OneToOne(targetEntity = TimeSeriesDate.class, cascade = CascadeType.PERSIST)
    private TimeSeriesDate date;

    private String series;

    private Integer version;
}
