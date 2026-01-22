package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(
  name = "tbl_project",
  indexes = {
      @Index(name = "idx_project_org", columnList = "organisation_id"),
      @Index(name = "idx_project_status", columnList = "status"),
      @Index(name = "idx_project_type", columnList = "project_type")
  }
)
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectMethodology methodology;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProjectType projectType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_manager_id")
    private Employee projectManager;

     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_lead_id")
    private Employee projectTeamLead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private Employee createdBy;


    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    // @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    // @Builder.Default
    // private List<Epic> epics = new ArrayList<>();

    // @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    // @Builder.Default
    // private List<Sprint> sprints = new ArrayList<>();

    // @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    // @Builder.Default
    // private List<Milestone> milestones = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
        if (status == null) status = ProjectStatus.PLANNING;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ProjectType {
        SOFTWARE_DEVELOPMENT("Software Development"),
        IT_SUPPORT("IT Support"),
        DESIGN("Design"),
        CUSTOMER_SERVICE("Customer Service"),
        HR("HR"),
        MARKETING("Marketing"),
        OPERATIONS("Operations"),
        SALES("Sales"),
        FINANCE("Finance"),
        OTHER_PERSONAL("Other/Personal");

        private final String displayName;

        ProjectType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ProjectMethodology {
    AGILE,
    SCRUM,
    KANBAN,
    WATERFALL,
    LEAN,
    PRINCE2,
    OTHER
}


    public enum ProjectStatus {
        PLANNING,
        IN_PROGRESS,
        ON_HOLD,
        COMPLETED,
        CANCELLED
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}