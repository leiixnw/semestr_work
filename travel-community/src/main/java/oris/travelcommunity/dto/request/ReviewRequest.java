package oris.travelcommunity.dto.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long proposalId;
    private Long targetUserId;
    private Integer rating;
    private String comment;
}