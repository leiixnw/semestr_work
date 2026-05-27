package oris.travelcommunity.repositories;

import oris.travelcommunity.models.TripProposal;

import java.math.BigDecimal;
import java.util.List;

public interface CustomTripProposalRepository  {

    List<TripProposal> findWithHighRatingApplicants();

    List<TripProposal> searchProposalsCustom(String location, String status);

    List<TripProposal> findByCriteria(String location, BigDecimal minPrice);
}
