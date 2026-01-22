package com.sellspark.SellsHRMS.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_designation", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "organisation_id", "department_id", "title" }),
        @UniqueConstraint(columnNames = { "role_id" }) 
    })

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id", unique = true)
    private Role role;

}
