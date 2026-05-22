package oris.travelcommunity.dto;

import lombok.*;
import oris.travelcommunity.models.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripProposalDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String mainImageUrl;
    private String status;
    private String organizerName;
    private List<Category> categoryNames;
}
