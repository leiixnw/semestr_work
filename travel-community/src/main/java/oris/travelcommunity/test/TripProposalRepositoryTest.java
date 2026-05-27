package oris.travelcommunity.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Тесты CustomTripProposalRepository")
@RequiredArgsConstructor
public class TripProposalRepositoryTest {

    private final TripProposalRepository tripProposalRepository;
    private final CustomTripProposalRepositoryImpl  customTripProposalRepository;

    private final UserRepository userRepository;

    private User organizer;

    @BeforeEach
    public void setUp() {
        tripProposalRepository.deleteAll();
        userRepository.deleteAll();

        User org = new User();
        org.setEmail("organizer@test.com");
        org.setUsername("organizer");
        org.setPassword("password123");
        org.setFullName("Test Organizer");
        org.setRole(UserRole.ROLE_ORGANIZER);
        organizer = userRepository.save(org);
    }

    @Test
    @DisplayName("CriteriaBuilder: поиск по локации и минимальной цене")
    public void testFindByCriteriaLocationAndPrice() {
        TripProposal proposal1 = new TripProposal();
        proposal1.setTitle("Швейцария");
        proposal1.setLocation("Швейцария");
        proposal1.setPrice(BigDecimal.valueOf(3000));
        proposal1.setStartDate(LocalDate.now().plusDays(10));
        proposal1.setEndDate(LocalDate.now().plusDays(17));
        proposal1.setMaxParticipants(5);
        proposal1.setCurrentParticipants(0);
        proposal1.setStatus(ProposalStatus.PLANNING);
        proposal1.setOrganizer(organizer);
        tripProposalRepository.save(proposal1);

        TripProposal proposal2 = new TripProposal();
        proposal2.setTitle("Франция");
        proposal2.setLocation("Франция");
        proposal2.setPrice(BigDecimal.valueOf(2000));
        proposal2.setStartDate(LocalDate.now().plusDays(20));
        proposal2.setEndDate(LocalDate.now().plusDays(27));
        proposal2.setMaxParticipants(4);
        proposal2.setCurrentParticipants(0);
        proposal2.setStatus(ProposalStatus.PLANNING);
        proposal2.setOrganizer(organizer);
        tripProposalRepository.save(proposal2);

        List<TripProposal> results = customTripProposalRepository.findByCriteria(
                "Швейцария",
                BigDecimal.valueOf(2500)
        );

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Швейцария", results.get(0).getLocation());
        assertEquals(BigDecimal.valueOf(3000), results.get(0).getPrice());
    }

    @Test
    @DisplayName("CriteriaBuilder: поиск только по локации")
    public void testFindByCriteriaLocationOnly() {
        TripProposal proposal = new TripProposal();
        proposal.setTitle("Испания");
        proposal.setLocation("Испания");
        proposal.setPrice(BigDecimal.valueOf(2500));
        proposal.setStartDate(LocalDate.now().plusDays(10));
        proposal.setEndDate(LocalDate.now().plusDays(17));
        proposal.setMaxParticipants(6);
        proposal.setCurrentParticipants(0);
        proposal.setStatus(ProposalStatus.PLANNING);
        proposal.setOrganizer(organizer);
        tripProposalRepository.save(proposal);

        List<TripProposal> results = customTripProposalRepository.findByCriteria(
                "Испания",
                null
        );

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Испания", results.get(0).getLocation());
    }

    @Test
    @DisplayName("CriteriaBuilder: поиск только по минимальной цене")
    public void testFindByCriteriaPriceOnly() {
        TripProposal proposal1 = new TripProposal();
        proposal1.setTitle("Тур 1");
        proposal1.setLocation("Страна 1");
        proposal1.setPrice(BigDecimal.valueOf(5000));
        proposal1.setStartDate(LocalDate.now().plusDays(10));
        proposal1.setEndDate(LocalDate.now().plusDays(17));
        proposal1.setMaxParticipants(5);
        proposal1.setCurrentParticipants(0);
        proposal1.setStatus(ProposalStatus.PLANNING);
        proposal1.setOrganizer(organizer);
        tripProposalRepository.save(proposal1);

        TripProposal proposal2 = new TripProposal();
        proposal2.setTitle("Тур 2");
        proposal2.setLocation("Страна 2");
        proposal2.setPrice(BigDecimal.valueOf(2000));
        proposal2.setStartDate(LocalDate.now().plusDays(20));
        proposal2.setEndDate(LocalDate.now().plusDays(27));
        proposal2.setMaxParticipants(4);
        proposal2.setCurrentParticipants(0);
        proposal2.setStatus(ProposalStatus.PLANNING);
        proposal2.setOrganizer(organizer);
        tripProposalRepository.save(proposal2);

        List<TripProposal> results = tripProposalRepository.findByCriteria(
                null,
                BigDecimal.valueOf(3000)
        );

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(BigDecimal.valueOf(5000), results.get(0).getPrice());
    }

    @Test
    @DisplayName("CriteriaBuilder: пустой результат если нет совпадений")
    public void testFindByCriteriaNoMatches() {
        TripProposal proposal = new TripProposal();
        proposal.setTitle("Тур");
        proposal.setLocation("Россия");
        proposal.setPrice(BigDecimal.valueOf(2000));
        proposal.setStartDate(LocalDate.now().plusDays(10));
        proposal.setEndDate(LocalDate.now().plusDays(17));
        proposal.setMaxParticipants(5);
        proposal.setCurrentParticipants(0);
        proposal.setStatus(ProposalStatus.PLANNING);
        proposal.setOrganizer(organizer);
        tripProposalRepository.save(proposal);

        List<TripProposal> results = customTripProposalRepository.findByCriteria(
                "Несуществующая страна",
                BigDecimal.valueOf(5000)
        );

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("CriteriaBuilder: поиск с null параметрами")
    public void testFindByCriteriaNullParameters() {
        TripProposal proposal = new TripProposal();
        proposal.setTitle("Тур");
        proposal.setLocation("Италия");
        proposal.setPrice(BigDecimal.valueOf(3000));
        proposal.setStartDate(LocalDate.now().plusDays(10));
        proposal.setEndDate(LocalDate.now().plusDays(17));
        proposal.setMaxParticipants(5);
        proposal.setCurrentParticipants(0);
        proposal.setStatus(ProposalStatus.PLANNING);
        proposal.setOrganizer(organizer);
        tripProposalRepository.save(proposal);

        List<TripProposal> results = customTripProposalRepository.findByCriteria(null, null);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("JPQL: поиск с подзапросом (высокорейтинговые путешественники)")
    public void testFindProposalsWithHighRatingApplicants() {
        TripProposal proposal = new TripProposal();
        proposal.setTitle("VIP Тур");
        proposal.setLocation("Мальдивы");
        proposal.setPrice(BigDecimal.valueOf(10000));
        proposal.setStartDate(LocalDate.now().plusDays(30));
        proposal.setEndDate(LocalDate.now().plusDays(37));
        proposal.setMaxParticipants(3);
        proposal.setCurrentParticipants(0);
        proposal.setStatus(ProposalStatus.PLANNING);
        proposal.setOrganizer(organizer);
        tripProposalRepository.save(proposal);

        List<TripProposal> results = tripProposalRepository.findWithHighRatingApplicants();

        assertNotNull(results);
    }

    @Test
    @DisplayName("JPQL: кастомный поиск по локации и статусу")
    public void testSearchProposalsCustom() {
        TripProposal proposal = new TripProposal();
        proposal.setTitle("Греция");
        proposal.setLocation("Греция");
        proposal.setPrice(BigDecimal.valueOf(2500));
        proposal.setStartDate(LocalDate.now().plusDays(15));
        proposal.setEndDate(LocalDate.now().plusDays(22));
        proposal.setMaxParticipants(4);
        proposal.setCurrentParticipants(0);
        proposal.setStatus(ProposalStatus.PLANNING);
        proposal.setOrganizer(organizer);
        tripProposalRepository.save(proposal);

        List<TripProposal> results = tripProposalRepository.searchProposalsCustom("Греция", "PLANNING");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Греция", results.get(0).getLocation());
        assertEquals("PLANNING", results.get(0).getStatus());
    }

    @Test
    @DisplayName("CriteriaBuilder: сортировка результатов")
    public void testFindByCriteriaWithSorting() {
        TripProposal proposal1 = new TripProposal();
        proposal1.setTitle("Тур 1");
        proposal1.setLocation("Япония");
        proposal1.setPrice(BigDecimal.valueOf(4000));
        proposal1.setStartDate(LocalDate.now().plusDays(20));
        proposal1.setEndDate(LocalDate.now().plusDays(27));
        proposal1.setMaxParticipants(5);
        proposal1.setCurrentParticipants(0);
        proposal1.setStatus(ProposalStatus.PLANNING);
        proposal1.setOrganizer(organizer);
        tripProposalRepository.save(proposal1);

        TripProposal proposal2 = new TripProposal();
        proposal2.setTitle("Тур 2");
        proposal2.setLocation("Япония");
        proposal2.setPrice(BigDecimal.valueOf(5000));
        proposal2.setStartDate(LocalDate.now().plusDays(30));
        proposal2.setEndDate(LocalDate.now().plusDays(37));
        proposal2.setMaxParticipants(3);
        proposal2.setCurrentParticipants(0);
        proposal2.setStatus(ProposalStatus.PLANNING);
        proposal2.setOrganizer(organizer);
        tripProposalRepository.save(proposal2);

        List<TripProposal> results = customTripProposalRepository.findByCriteria("Япония", null);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.get(0).getStartDate().isBefore(results.get(1).getStartDate()));
    }
}