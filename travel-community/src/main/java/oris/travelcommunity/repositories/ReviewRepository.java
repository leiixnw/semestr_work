package oris.travelcommunity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import oris.travelcommunity.models.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByProposalId(Long proposalId);
    List<Review> findByTargetUserId(Long userId);
}
