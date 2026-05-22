package oris.travelcommunity.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class TripProposalRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private List<Long> categoryIds;
}
