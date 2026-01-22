package com.sellspark.SellsHRMS.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_employee_shift")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeShift {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo; // null = current
    @Enumerated(EnumType.STRING)
    private Mode mode;

    public enum Mode { MANUAL, AUTOMATIC }
}

