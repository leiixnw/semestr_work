package oris.travelcommunity.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import oris.travelcommunity.dto.request.TripApplicationRequest;
import oris.travelcommunity.exceptions.NotFoundException;
import oris.travelcommunity.models.TripApplication;
import oris.travelcommunity.models.TripProposal;
import oris.travelcommunity.models.User;
import oris.travelcommunity.models.enums.ApplicationStatus;
import oris.travelcommunity.repositories.TripApplicationRepository;
import oris.travelcommunity.repositories.TripProposalRepository;
import oris.travelcommunity.services.TripApplicationService;
import oris.travelcommunity.services.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripApplicationServiceImpl implements TripApplicationService {

    private final TripApplicationRepository applicationRepository;
    private final TripProposalRepository proposalRepository;
    private final UserService userService;

    @Override
    @Transactional
    public TripApplication create(TripApplicationRequest applicationRequest, Long travelerId) {
        TripProposal proposal = proposalRepository.findById(applicationRequest.getProposalId())
                .orElseThrow(() -> new NotFoundException("Trip proposal not found"));
        User traveler = userService.getById(travelerId);

        TripApplication tripApplication = TripApplication.builder()
                .proposal(proposal)
                .traveler(traveler)
                .status(ApplicationStatus.PENDING)
                .comment(applicationRequest.getComment())
                .build();

        return applicationRepository.save(tripApplication);
    }

    @Override
    @Transactional
    public TripApplication changeStatus(Long applicationId, Long organizerId, ApplicationStatus status) {
        TripApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        TripProposal proposal = application.getProposal();

        if (!proposal.getOrganizer().getId().equals(organizerId)) {
            throw new SecurityException("You are not allowed to change this application");
        }

        if (status == ApplicationStatus.APPROVED && application.getStatus() != ApplicationStatus.APPROVED) {
            if (proposal.getCurrentParticipants() >= proposal.getMaxParticipants()) {
                throw new IllegalStateException("The application is full");
            }
            proposal.setCurrentParticipants(proposal.getCurrentParticipants() + 1);
            proposalRepository.save(proposal);
        }

        if (status == ApplicationStatus.REJECTED && application.getStatus() != ApplicationStatus.REJECTED) {
            proposal.setCurrentParticipants(proposal.getCurrentParticipants() - 1);
            proposalRepository.save(proposal);
        }

        application.setStatus(status);
        application.setDecidedAt(LocalDateTime.now());
        return  applicationRepository.save(application);
    }

    @Override
    public List<TripApplication> getByProposalId(Long proposalId) {
        return applicationRepository.findByProposalId(proposalId);
    }
}
