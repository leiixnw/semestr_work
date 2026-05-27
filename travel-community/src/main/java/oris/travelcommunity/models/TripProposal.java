package oris.travelcommunity.models;

import jakarta.persistence.*;
import lombok.*;
import oris.travelcommunity.models.enums.ProposalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trip_proposals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants = 1;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 0;

    @Column(name = "main_image_url", length = 500)
    private String mainImageUrl;

    @Column(nullable = false, length = 50)
    private ProposalStatus status = ProposalStatus.PLANNING;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.valueOf(0.0);

    @Column(name = "reviews_count", nullable = false)
    private Integer reviewsCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "trip_proposal_categories",
            joinColumns = @JoinColumn(name = "proposal_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
