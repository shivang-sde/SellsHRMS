package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_module")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // e.g. ORG_LIST, ORG_ADMIN, EMP_LIST, ATTENDANCE

    @Column(nullable = false)
    private String name; // human readable
}
