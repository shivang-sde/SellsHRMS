package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_employee_leave_balance",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"employee_id","leave_type_id","leave_year"})})
public class EmployeeLeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE) 
    private LeaveType leaveType;

    @Column(name = "leave_year", length = 20)
    private String leaveYear;

    // @Column(name = "financial_year", length = 20)
    // private String financialYear;

    @Builder.Default
    private Double openingBalance = 0.0;

    @Builder.Default
    private Double accrued = 0.0;

    @Builder.Default
    private Double availed = 0.0;
     @Builder.Default
    private Double carriedForward = 0.0;
     @Builder.Default
    private Double encashed = 0.0;

     @Builder.Default
    private Double closingBalance = 0.0;

    @Column(name = "last_updated_on")
    private LocalDateTime lastUpdatedOn;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private BalanceStatus balanceStatus = BalanceStatus.ACTIVE;

    public enum BalanceStatus {
        ACTIVE, LOCKED, ARCHIVED
    }
}
