package oris.travelcommunity.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import oris.travelcommunity.models.enums.ApplicationStatus;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "trip_applications", uniqueConstraints = {
        @UniqueConstraint(name = "uq_proposal_traveler", columnNames = {"proposal_id", "traveler_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private TripProposal proposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traveler_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User traveler;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(length = 1000)
    private String comment;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;
}
