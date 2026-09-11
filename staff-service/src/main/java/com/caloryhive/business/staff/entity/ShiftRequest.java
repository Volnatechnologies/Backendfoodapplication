package com.caloryhive.business.staff.entity;

import com.caloryhive.business.common.AuditEntity;
import com.caloryhive.business.staff.entity.enums.RequestStatus;
import com.caloryhive.business.staff.entity.enums.RequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "shift_requests", indexes = {
        @Index(name = "idx_requests_staff", columnList = "staff_id"),
        @Index(name = "idx_requests_shift", columnList = "shift_id"),
        @Index(name = "idx_requests_target_staff", columnList = "target_staff_id"),
        @Index(name = "idx_requests_status", columnList = "status"),
        @Index(name = "idx_requests_type", columnList = "request_type"),
        @Index(name = "idx_requests_date", columnList = "requested_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ShiftRequest extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Requesting staff member is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnoreProperties({"shifts", "requests", "hibernateLazyInitializer", "handler"})
    private Staff staff;

    @NotNull(message = "Request type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 30)
    private RequestType requestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    @JsonIgnoreProperties({"staff", "hibernateLazyInitializer", "handler"})
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_staff_id")
    @JsonIgnoreProperties({"shifts", "requests", "hibernateLazyInitializer", "handler"})
    private Staff targetStaff;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "requested_date")
    private LocalDate requestedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    @Column(name = "reason", length = 500)
    private String reason;

    @NotNull(message = "Request status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Size(max = 500)
    @Column(name = "admin_comment", length = 500)
    private String adminComment;
}
