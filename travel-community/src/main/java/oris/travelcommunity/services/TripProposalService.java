package oris.travelcommunity.services;

import oris.travelcommunity.dto.TripProposalDto;
import oris.travelcommunity.dto.request.TripProposalRequest;

import java.math.BigDecimal;
import java.util.List;

public interface TripProposalService {
    List<TripProposalDto> getAllActiveProposals();
    TripProposalDto getProposalById(Long id);
    List<TripProposalDto> getHotTrips();
    BigDecimal getPriceInUsd(Long proposalId);
    TripProposalDto create(TripProposalRequest request, String organizerEmail);
    TripProposalDto update(Long id, TripProposalRequest request, String organizerEmail);
    void delete(Long id, String organizerEmail);
}
