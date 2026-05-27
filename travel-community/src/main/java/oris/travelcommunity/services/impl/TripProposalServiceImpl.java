package oris.travelcommunity.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oris.travelcommunity.dto.TripProposalDto;
import oris.travelcommunity.exceptions.NotFoundException;
import oris.travelcommunity.models.Category;
import oris.travelcommunity.models.TripProposal;
import oris.travelcommunity.repositories.TripProposalRepository;
import oris.travelcommunity.services.CurrencyConversionService;
import oris.travelcommunity.services.TripProposalService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripProposalServiceImpl implements TripProposalService {

    private final TripProposalRepository tripProposalRepository;
    private final CurrencyConversionService currencyService;

    @Override
    // ТЗ: Кэширование тяжелых или часто запрашиваемых данных в Redis
    @Cacheable(value = "activeProposals", key = "'all'")
    public List<TripProposalDto> getAllActiveProposals() {
        return tripProposalRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TripProposalDto getProposalById(Long id) {
        TripProposal proposal = tripProposalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Поездка с id " + id + " не найдена"));
        return convertToDto(proposal);
    }

    @Override
    public List<TripProposalDto> getHotTrips() {
        return tripProposalRepository.findHotTrips().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getPriceInUsd(Long proposalId) {
        TripProposal proposal = tripProposalRepository.findById(proposalId)
                .orElseThrow(() -> new NotFoundException("Поездка не найдена"));
        return currencyService.convertRubToUsd(proposal.getPrice());
    }

    private TripProposalDto convertToDto(TripProposal entity) {
        return TripProposalDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .location(entity.getLocation())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .maxParticipants(entity.getMaxParticipants())
                .currentParticipants(entity.getCurrentParticipants())
                .mainImageUrl(entity.getMainImageUrl())
                .status(entity.getStatus())
                .organizerName(entity.getOrganizer().getFullName())
                .categoryNames(entity.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}