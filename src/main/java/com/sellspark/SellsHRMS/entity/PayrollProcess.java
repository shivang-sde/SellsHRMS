package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_payroll_process")
public class PayrollProcess {
    @Id @GeneratedValue private Long id;
    @ManyToOne(fetch = FetchType.LAZY) private Organisation organisation;
    @ManyToOne(fetch = FetchType.LAZY) private Employee employee;

    private String month;
    private String financialYear;
    private Double grossPay;
    private Double lopDays;
    private Double overtimePay;
    private Double encashmentPay;

    private LocalDate processedOn;
    private String status; // DRAFT, APPROVED, FINALIZE
}
