package oris.travelcommunity.dto.request;

import lombok.Data;

@Data
public class TripApplicationRequest {
    private Long proposalId;
    private String comment;
}