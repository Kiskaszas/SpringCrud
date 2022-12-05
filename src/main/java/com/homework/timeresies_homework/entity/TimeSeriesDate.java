package com.homework.timeresies_homework.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "time_series_date")
public class TimeSeriesDate implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "time_series_date_generator")
    @SequenceGenerator(name="time_series_date_generator", sequenceName = "time_series_date_seq")
    @JsonProperty
    private Long id;

    @Column
    @OneToMany(targetEntity = PowerStation.class,cascade = CascadeType.PERSIST)
    @JsonProperty
    private Set<PowerStation> powerStation;

    @Column
    @JsonProperty
    private String date;

    @Column(unique = true)
    @JsonProperty
    private Timestamp timestamp;

    @Column
    @JsonProperty
    private String zone;

    @Column
    @JsonProperty
    private String period;
}
