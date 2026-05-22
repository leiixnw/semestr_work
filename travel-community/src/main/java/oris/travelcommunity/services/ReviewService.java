package oris.travelcommunity.services;

import oris.travelcommunity.models.Review;

public interface ReviewService {
    Review leave(Long authorId, Long proposalId, Long targetUserId, Integer rating, String comment);
}
