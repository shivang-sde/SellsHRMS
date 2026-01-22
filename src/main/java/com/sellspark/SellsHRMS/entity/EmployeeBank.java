package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_bank")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One employee → multiple bank accounts
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false, length = 150)
    private String bankName;

    @Column(nullable = false, length = 50)
    private String accountNumber;

    @Column(nullable = false, length = 50)
    private String ifscCode;


    @Column(length = 100)
    private String upiId;

    @Column(length = 100)
    private String branch;

    @Column(nullable = false)
    private Boolean isPrimaryAccount = false;
}
