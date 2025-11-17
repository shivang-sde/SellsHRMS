package com.sellspark.SellsHRMS.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_designation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    private String title;
    private String description;
}
