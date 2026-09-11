package com.caloryhive.business.catering.entity;

import com.caloryhive.business.common.AuditEntity;
import com.caloryhive.business.common.enums.InquiryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "catering_inquiries", indexes = {
        @Index(name = "idx_inquiry_status", columnList = "status"),
        @Index(name = "idx_inquiry_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class CateringInquiry extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "guest_count")
    private Integer guestCount;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private InquiryStatus status = InquiryStatus.NEW;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}


