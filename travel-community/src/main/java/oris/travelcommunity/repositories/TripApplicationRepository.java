package oris.travelcommunity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import oris.travelcommunity.models.TripApplication;

import java.util.List;

public interface TripApplicationRepository extends JpaRepository<TripApplication, Long> {
    List<TripApplication> findByProposalId(Long proposalId);
    List<TripApplication> findByTravelerId(Long travelerId);
}
