package com.sellspark.SellsHRMS.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_punch_in_out", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "employee_id", "attendance_date" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PunchInOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private Double lat;
    private Double lng;

    @Enumerated(EnumType.STRING)
    private PUNCHFROM punchedFrom;

    private Instant punchIn;
    private Instant punchOut;
    private Double workHours;

    @Enumerated(EnumType.STRING)
    private Source punchSource;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Builder.Default
    private Boolean isAutoPunchGenerated = false;

    private String location;

    public enum Source {
        WEB, MOBILE, BIOMETRIC
    }

    public enum PUNCHFROM {
        WFH, WFO
    }
}
