package oris.travelcommunity.models;

import jakarta.persistence.*;
import lombok.*;
import oris.travelcommunity.models.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String fullName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(nullable = false)
    private BigDecimal rating = BigDecimal.valueOf(0.0);

    @Column(nullable = false)
    private Integer reviewsCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.ROLE_TRAVELER;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private List<TripProposal> organizedTrips;
}
