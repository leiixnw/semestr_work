package oris.travelcommunity;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import oris.travelcommunity.models.TripProposal;
import oris.travelcommunity.models.User;
import oris.travelcommunity.models.enums.ProposalStatus;
import oris.travelcommunity.models.enums.UserRole;
import oris.travelcommunity.repositories.TripProposalRepository;
import oris.travelcommunity.repositories.UserRepository;
import oris.travelcommunity.repositories.impl.CustomTripProposalRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Тесты CustomTripProposalRepository")
@RequiredArgsConstructor
public class TripProposalRepositoryTest {

    private final TripProposalRepository tripProposalRepository;
    private final CustomTripProposalRepositoryImpl customTripProposalRepository;
    private final UserRepository userRepository;
    private User organizer;

    @BeforeEach
    public void setUp() {
        tripProposalRepository.deleteAll();
        userRepository.deleteAll();

        User org = User.builder()
                .email("organizer@test.com")
                .username("organizer")
                .password("password123")
                .fullName("Test Organizer")
                .role(UserRole.ROLE_ORGANIZER)
                .build();
        organizer = userRepository.save(org);
    }

    private TripProposal buildProposal(String title, String location, BigDecimal price) {
        return TripProposal.builder()
                .title(title)
                .description("desc")
                .location(location)
                .price(price)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(17))
                .maxParticipants(5)
                .organizer(organizer)
                .build();
    }

    @Test
    @DisplayName("CriteriaBuilder: поиск по локации и минимальной цене")
    public void testFindByCriteriaLocationAndPrice() {
        tripProposalRepository.save(buildProposal("Швейцария", "Швейцария", BigDecimal.valueOf(3000)));
        tripProposalRepository.save(buildProposal("Франция", "Франция", BigDecimal.valueOf(2000)));

        List<TripProposal> results = customTripProposalRepository.findByCriteria("Швейцария", BigDecimal.valueOf(2500));

        assertEquals(1, results.size());
        assertEquals("Швейцария", results.get(0).getLocation());
    }

    @Test
    @DisplayName("CriteriaBuilder: поиск только по локации")
    public void testFindByCriteriaLocationOnly() {
        tripProposalRepository.save(buildProposal("Испания", "Испания", BigDecimal.valueOf(2500)));

        List<TripProposal> results = customTripProposalRepository.findByCriteria("Испания", null);

        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("CriteriaBuilder: пустой результат если нет совпадений")
    public void testFindByCriteriaNoMatches() {
        tripProposalRepository.save(buildProposal("Тур", "Россия", BigDecimal.valueOf(2000)));

        List<TripProposal> results = customTripProposalRepository.findByCriteria("Несуществующая страна", BigDecimal.valueOf(5000));

        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("JPQL: кастомный поиск по локации и статусу")
    public void testSearchProposalsCustom() {
        tripProposalRepository.save(buildProposal("Греция", "Греция", BigDecimal.valueOf(2500)));

        List<TripProposal> results = tripProposalRepository.searchProposalsCustom("Греция", "PLANNING");

        assertFalse(results.isEmpty());
        assertEquals("Греция", results.get(0).getLocation());
        assertEquals(ProposalStatus.PLANNING, results.get(0).getStatus());
    }

    @Test
    @DisplayName("JPQL: поиск туров с высокорейтинговыми участниками")
    public void testFindProposalsWithHighRatingApplicants() {
        tripProposalRepository.save(buildProposal("VIP Тур", "Мальдивы", BigDecimal.valueOf(10000)));

        List<TripProposal> results = tripProposalRepository.findWithHighRatingApplicants();
        assertNotNull(results);
    }
}
