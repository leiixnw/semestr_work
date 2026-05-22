package oris.travelcommunity.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import oris.travelcommunity.exceptions.NotFoundException;
import oris.travelcommunity.models.Review;
import oris.travelcommunity.models.TripProposal;
import oris.travelcommunity.models.User;
import oris.travelcommunity.repositories.ReviewRepository;
import oris.travelcommunity.repositories.TripProposalRepository;
import oris.travelcommunity.repositories.UserRepository;
import oris.travelcommunity.services.ReviewService;
import oris.travelcommunity.services.UserService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TripProposalRepository proposalRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Review leave(Long authorId, Long proposalId, Long targetUserId, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Author not found"));
        TripProposal proposal = proposalRepository.findById(proposalId)
                .orElse(null);
        User targetUser = userRepository.findById(targetUserId)
                .orElse(null);

        Review review = Review.builder()
                .author(author)
                .proposal(proposal)
                .targetUser(targetUser)
                .rating(rating)
                .comment(comment)
                .build();
        Review savedReview = reviewRepository.save(review);

        if (targetUser != null) {
            List<Review> userReviews = reviewRepository.findByTargetUserId(targetUserId);
            double avg = userReviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            targetUser.setRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
            targetUser.setReviewsCount(userReviews.size());
            userRepository.save(targetUser);
        }

        if (proposal != null) {
            List<Review> proposalReviews = reviewRepository.findByProposalId(proposalId);
            double avg = proposalReviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            proposal.setRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
            proposal.setReviewsCount(proposalReviews.size());
            proposalRepository.save(proposal);
        }

        return savedReview;
    }
}
