package oris.travelcommunity.services;

import oris.travelcommunity.dto.request.TripApplicationRequest;
import oris.travelcommunity.dto.request.TripProposalRequest;
import oris.travelcommunity.models.TripApplication;
import oris.travelcommunity.models.enums.ApplicationStatus;

import java.util.List;

public interface TripApplicationService {
    TripApplication create(TripApplicationRequest applicationRequest, Long travelerId);
    TripApplication changeStatus(Long applicationId, Long organizerId, ApplicationStatus status);
    List<TripApplication> getByProposalId(Long proposalId);
}
