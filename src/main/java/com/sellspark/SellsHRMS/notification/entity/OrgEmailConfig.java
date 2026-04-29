package com.sellspark.SellsHRMS.notification.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.notification.util.SmtpPasswordConverter;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_org_email_config")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgEmailConfig {

    @Id
    @Column(name = "org_id")
    private Long orgId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "org_id", nullable = false)
    private Organisation organisation;

    @Column(name = "smtp_host")
    private String smtpHost;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "smtp_username")
    private String smtpUsername;

    @Column(name = "smtp_password")
    @Convert(converter = SmtpPasswordConverter.class)
    private String smtpPassword;

    @Column(name = "use_tls")
    @Builder.Default
    private Boolean useTls = true;

    @Column(name = "use_ssl")
    @Builder.Default
    private Boolean useSsl = false;

    @Column(name = "from_email")
    private String fromEmail;

    @Column(name = "from_name")
    private String fromName;

    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "daily_limit")
    @Builder.Default
    private Integer dailyLimit = 100;

    @Column(name = "hourly_limit")
    @Builder.Default
    private Integer hourlyLimit = 20;

    @Column(name = "sent_today")
    @Builder.Default
    private Integer sentToday = 0;

    @Column(name = "sent_this_hour")
    @Builder.Default
    private Integer sentThisHour = 0;

    @Column(name = "last_reset_at")
    private LocalDateTime lastResetAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // helper methods for rate limting
    public boolean canSendEmail() {
        return isActive
                && sentToday < dailyLimit
                && sentThisHour < hourlyLimit;
    }

    public void incrementSentCounters() {
        this.sentToday = (this.sentToday != null ? this.sentToday : 0) + 1;
        this.sentThisHour = (this.sentThisHour != null ? this.sentThisHour : 0) + 1;
    }

    public void resetHourlyCounterIfNeeded() {
        if (lastResetAt == null || lastResetAt.isBefore(LocalDateTime.now().minusHours(1))) {
            this.sentThisHour = 0;
            this.lastResetAt = LocalDateTime.now();
        }
    }

    public void resetDailyCounter() {
        if (lastResetAt == null || lastResetAt.isBefore(LocalDateTime.now().minusDays(1))) {
            this.sentToday = 0;
            this.lastResetAt = LocalDateTime.now();
        }
    }

}
