package oris.travelcommunity.services;

import oris.travelcommunity.dto.TripProposalDto;

import java.math.BigDecimal;
import java.util.List;

public interface TripProposalService {
    List<TripProposalDto> getAllActiveProposals();
    TripProposalDto getProposalById(Long id);
    List<TripProposalDto> getHotTrips();
    BigDecimal getPriceInUsd(Long proposalId);
}