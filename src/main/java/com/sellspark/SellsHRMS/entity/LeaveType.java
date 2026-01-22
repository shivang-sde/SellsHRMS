package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_leave_type",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"organisation_id", "name"})})
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    private String description;

    private Integer annualLimit;
    private Boolean isPaid;

    private Boolean carryForwardAllowed;
    private Integer carryForwardLimit; // e.g., max 30
    private Boolean encashable;
    private Boolean requiresApproval;
    private Boolean availableDuringProbation;
    @Builder.Default
    private Boolean allowDuringNoticePeriod = false;
    
    private Boolean allowHalfDay;
    private Boolean includeHolidaysInLeave; // policy toggle
    @Builder.Default
    private Boolean visibleToEmployees = true;
    private Integer maxConsecutiveDays;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private AccrualMethod accrualMethod; // MONTHLY, ANNUAL, PRO_RATA, NONE

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ApplicableGender applicableGender; // MALE, FEMALE, ALL

     @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ResetCycle resetCycle;        // YEARLY, MONTHLY, WEEKLY, NONE

    private Double accrualRate; // e.g., 1.5 per month
    private Integer validityDays; // for comp-off or special leaves
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    public enum ResetCycle {
        YEARLY, 
        MONTHLY, 
        WEEKLY,
        NONE
    }


    public enum AccrualMethod {
    MONTHLY,
    ANNUAL,
    PRO_RATA,
    NONE
}
    public enum ApplicableGender {
    MALE,
    FEMALE,
    ALL 
}
}
