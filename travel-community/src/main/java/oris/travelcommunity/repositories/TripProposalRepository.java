package oris.travelcommunity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import oris.travelcommunity.models.TripProposal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripProposalRepository extends JpaRepository<TripProposal, Long>, CustomTripProposalRepository {

    @Query("SELECT tp FROM TripProposal tp WHERE tp.status = 'PLANNING' AND tp.currentParticipants < (tp.maxParticipants / 2)")
    List<TripProposal> findHotTrips();

    @Query("SELECT tp FROM TripProposal tp JOIN tp.categories c WHERE c.id = :categoryId AND tp.startDate >= :now")
    List<TripProposal> findFutureTripsByCategory(@Param("categoryId") Long categoryId, @Param("now") LocalDate now);

}