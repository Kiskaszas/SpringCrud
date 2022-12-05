package com.homework.timeresies_homework.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "power_station")
public class PowerStation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @JsonProperty("power-station-name")
    private String powerStationName;

    @JsonIgnore
    @OneToMany(targetEntity = TimeSeriesDate.class,cascade = CascadeType.PERSIST)
    private Set<TimeSeriesDate> timeSeriesDate;
}
