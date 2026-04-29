package com.sellspark.SellsHRMS.notification.entity;

import com.sellspark.SellsHRMS.notification.enums.TargetRole;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_notification_templates", uniqueConstraints = @UniqueConstraint(columnNames = {
        "event_code",
        "target_role" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventCode;

    @Enumerated(EnumType.STRING)
    private TargetRole targetRole;

    @Column(nullable = false, length = 500)
    private String subject;

    @Lob
    @Column(nullable = false, length = 20000, columnDefinition = "LONGTEXT")
    private String body; // HTML template

    @Builder.Default
    private Boolean isActive = true;

}
