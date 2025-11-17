package com.sellspark.SellsHRMS.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_permission")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String module;      
    private String action;      

    @Column(unique = true)
    private String code;        
}

