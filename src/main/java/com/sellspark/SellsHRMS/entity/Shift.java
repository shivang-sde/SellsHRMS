package com.sellspark.SellsHRMS.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "tbl_shift")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shift {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer breakMinutes;
    private Boolean isNightShift = false;
}
