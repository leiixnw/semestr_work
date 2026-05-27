package oris.travelcommunity;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import oris.travelcommunity.dto.SignUpForm;
import oris.travelcommunity.dto.request.TripApplicationRequest;
import oris.travelcommunity.exceptions.NotFoundException;
import oris.travelcommunity.models.TripApplication;
import oris.travelcommunity.models.TripProposal;
import oris.travelcommunity.models.User;
import oris.travelcommunity.models.enums.ApplicationStatus;
import oris.travelcommunity.models.enums.UserRole;
import oris.travelcommunity.repositories.TripApplicationRepository;
import oris.travelcommunity.repositories.TripProposalRepository;
import oris.travelcommunity.repositories.UserRepository;
import oris.travelcommunity.services.TripApplicationService;
import oris.travelcommunity.services.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Тесты TripApplicationService")
@RequiredArgsConstructor
public class TripApplicationServiceTest {

    private final TripApplicationService tripApplicationService;
    private final UserService userService;
    private final TripApplicationRepository applicationRepository;
    private final TripProposalRepository proposalRepository;
    private final UserRepository userRepository;

    private User traveler;
    private User organizer;
    private TripProposal proposal;

    @BeforeEach
    public void setUp() {
        applicationRepository.deleteAll();
        proposalRepository.deleteAll();
        userRepository.deleteAll();

        SignUpForm travelerForm = new SignUpForm();
        travelerForm.setEmail("traveler@test.com");
        travelerForm.setUsername("traveler");
        travelerForm.setPassword("password123");
        travelerForm.setFullName("Test Traveler");
        travelerForm.setRole(UserRole.ROLE_TRAVELER);
        traveler = userService.register(travelerForm);

        SignUpForm organizerForm = new SignUpForm();
        organizerForm.setEmail("organizer@test.com");
        organizerForm.setUsername("organizer");
        organizerForm.setPassword("password456");
        organizerForm.setFullName("Test Organizer");
        organizerForm.setRole(UserRole.ROLE_ORGANIZER);
        organizer = userService.register(organizerForm);

        proposal = TripProposal.builder()
                .title("Путешествие в Швейцарию")
                .description("Красивое путешествие на 7 дней")
                .location("Швейцария")
                .startDate(LocalDate.now().plusDays(30))
                .endDate(LocalDate.now().plusDays(37))
                .maxParticipants(5)
                .price(BigDecimal.valueOf(2500))
                .organizer(organizer)
                .build();
        proposal = proposalRepository.save(proposal);
    }

    @Test
    @DisplayName("Успешное создание заявки на тур")
    public void testCreateApplicationSuccess() {
        TripApplicationRequest request = new TripApplicationRequest();
        request.setProposalId(proposal.getId());
        request.setComment("Хочу присоединиться!");

        TripApplication application = tripApplicationService.create(request, traveler.getId());

        assertNotNull(application);
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertEquals("Хочу присоединиться!", application.getComment());
    }

    @Test
    @DisplayName("Заявка на несуществующий тур выбрасывает исключение")
    public void testCreateApplicationForNonexistentProposal() {
        TripApplicationRequest request = new TripApplicationRequest();
        request.setProposalId(99999L);
        request.setComment("Test");

        assertThrows(NotFoundException.class, () -> tripApplicationService.create(request, traveler.getId()));
    }

    @Test
    @DisplayName("Изменение статуса заявки на APPROVED")
    public void testChangeApplicationStatusToApproved() {
        TripApplicationRequest request = new TripApplicationRequest();
        request.setProposalId(proposal.getId());
        request.setComment("Test");
        TripApplication application = tripApplicationService.create(request, traveler.getId());

        TripApplication updated = tripApplicationService.changeStatus(application.getId(), organizer.getId(), ApplicationStatus.APPROVED);

        assertEquals(ApplicationStatus.APPROVED, updated.getStatus());
    }

    @Test
    @DisplayName("Получение всех заявок по туру")
    public void testGetApplicationsByProposalId() {
        TripApplicationRequest request = new TripApplicationRequest();
        request.setProposalId(proposal.getId());
        request.setComment("Comment");
        tripApplicationService.create(request, traveler.getId());

        List<TripApplication> applications = tripApplicationService.getByProposalId(proposal.getId());
        assertEquals(1, applications.size());
    }
}
