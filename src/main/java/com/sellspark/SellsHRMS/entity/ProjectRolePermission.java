package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_project_role_permission")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectRolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The role this permission belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private ProjectRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProjectPermission permission;

    public enum ProjectPermission {
        MANAGE_PROJECT,       // create/edit project, add members
        CREATE_TASK,
        UPDATE_TASK,
        CHANGE_STATUS,
        ASSIGN_TASK,
        COMMENT,
        UPLOAD_ATTACHMENT,
        MANAGE_SPRINTS,
        MANAGE_EPICS,
        MANAGE_MILESTONES,
        VIEW_ONLY
    }
}
