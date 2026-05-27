package oris.travelcommunity.services;

import lombok.RequiredArgsConstructor;
import org.checkerframework.framework.qual.RequiresQualifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import oris.travelcommunity.dto.SignUpForm;
import oris.travelcommunity.dto.request.TripApplicationRequest;
import oris.travelcommunity.exceptions.NotFoundException;
import oris.travelcommunity.models.*;
import oris.travelcommunity.models.enums.ApplicationStatus;
import oris.travelcommunity.models.enums.ProposalStatus;
import oris.travelcommunity.models.enums.UserRole;
import oris.travelcommunity.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
@RequiredArgsConstructor
@DisplayName("Тесты TripApplicationService")
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

        proposal = new TripProposal();
        proposal.setTitle("Путешествие в Швейцарию");
        proposal.setDescription("Красивое путешествие на 7 дней");
        proposal.setLocation("Швейцария");
        proposal.setStartDate(LocalDate.now().plusDays(30));
        proposal.setEndDate(LocalDate.now().plusDays(37));
        proposal.setMaxParticipants(5);
        proposal.setCurrentParticipants(0);
        proposal.setPrice(BigDecimal.valueOf(2500));
        proposal.setStatus(ProposalStatus.PLANNING);
        proposal.setOrganizer(organizer);
        proposal = proposalRepository.save(proposal);
    }

    @Test
    @DisplayName("Успешное создание заявки на тур")
    public void testCreateApplicationSuccess() {
        TripApplicationRequest request = new TripApplicationRequest();
        request.setProposalId(proposal.getId());
        request.setComment("Очень хочу присоединиться к этому путешествию!");

        TripApplication application = tripApplicationService.create(request, traveler.getId());

        assertNotNull(application);
        assertEquals(proposal.getId(), application.getProposal().getId());
        assertEquals(traveler.getId(), application.getTraveler().getId());
        assertEquals("Очень хочу присоединиться к этому путешествию!", application.getComment());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
    }

    @Test
    @DisplayName("Создание заявки на несуществующий тур выбрасывает исключение")
    public void testCreateApplicationForNonexistentProposal() {
        TripApplicationRequest request = new TripApplicationRequest();
        request.setProposalId(99999L);
        request.setComment("Test comment");

        assertThrows(NotFoundException.class, () -> {
            tripApplicationService.create(request, traveler.getId());
        });
    }

    @Test
    @DisplayName("Изменение статуса заявки на APPROVED")
    public void testChangeApplicationStatusToApproved() {
        TripApplicationRequest request = new TripApplicationRequest();
        request.setProposalId(proposal.getId());
        request.setComment("Test");
        TripApplication application = tripApplicationService.create(request, traveler.getId());

        TripApplication updated = tripApplicationService.changeStatus(
                application.getId(),
                organizer.getId(),
                ApplicationStatus.APPROVED
        );

        assertNotNull(updated);
        assertEquals(ApplicationStatus.APPROVED, updated.getStatus());
    }

    @Test
    @DisplayName("Изменение статуса заявки на REJECTED")
    public void testChangeApplicationStatusToRejected() {
        TripApplicationRequest request = new TripApplicationRequest();
        request.setProposalId(proposal.getId());
        request.setComment("Test");
        TripApplication application = tripApplicationService.create(request, traveler.getId());

        TripApplication updated = tripApplicationService.changeStatus(
                application.getId(),
                organizer.getId(),
                ApplicationStatus.REJECTED
        );

        assertNotNull(updated);
        assertEquals(ApplicationStatus.REJECTED, updated.getStatus());
    }

    @Test
    @DisplayName("Получение всех заявок по туру")
    public void testGetApplicationsByProposalId() {
        TripApplicationRequest request1 = new TripApplicationRequest();
        request1.setProposalId(proposal.getId());
        request1.setComment("Comment 1");

        TripApplicationRequest request2 = new TripApplicationRequest();
        request2.setProposalId(proposal.getId());
        request2.setComment("Comment 2");

        tripApplicationService.create(request1, traveler.getId());

        SignUpForm form2 = new SignUpForm();
        form2.setEmail("traveler2@test.com");
        form2.setUsername("traveler2");
        form2.setPassword("password789");
        form2.setFullName("Test Traveler 2");
        form2.setRole(UserRole.ROLE_TRAVELER);
        User traveler2 = userService.register(form2);

        tripApplicationService.create(request2, traveler2.getId());

        List<TripApplication> applications = tripApplicationService.getByProposalId(proposal.getId());

        assertNotNull(applications);
        assertEquals(2, applications.size());
    }

    @Test
    @DisplayName("Пустой список заявок для тура без заявок")
    public void testGetApplicationsForProposalWithoutApplications() {
        List<TripApplication> applications = tripApplicationService.getByProposalId(proposal.getId());

        assertNotNull(applications);
        assertEquals(0, applications.size());
    }

    @Test
    @DisplayName("Заявка содержит корректные данные")
    public void testApplicationContainsCorrectData() {
        TripApplicationRequest request = new TripApplicationRequest();
        request.setProposalId(proposal.getId());
        request.setComment("Детальный комментарий с особыми пожеланиями");

        TripApplication application = tripApplicationService.create(request, traveler.getId());

        assertNotNull(application.getCreatedAt());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertEquals("Детальный комментарий с особыми пожеланиями", application.getComment());
        assertEquals(proposal.getId(), application.getProposal().getId());
        assertEquals(traveler.getId(), application.getTraveler().getId());
    }
}