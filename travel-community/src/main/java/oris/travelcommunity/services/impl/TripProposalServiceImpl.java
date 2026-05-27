package oris.travelcommunity.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oris.travelcommunity.dto.TripProposalDto;
import oris.travelcommunity.dto.request.TripProposalRequest;
import oris.travelcommunity.exceptions.NotFoundException;
import oris.travelcommunity.models.Category;
import oris.travelcommunity.models.TripProposal;
import oris.travelcommunity.models.User;
import oris.travelcommunity.repositories.CategoryRepository;
import oris.travelcommunity.repositories.TripProposalRepository;
import oris.travelcommunity.repositories.UserRepository;
import oris.travelcommunity.services.CurrencyConversionService;
import oris.travelcommunity.services.TripProposalService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripProposalServiceImpl implements TripProposalService {

    private final TripProposalRepository tripProposalRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CurrencyConversionService currencyService;

    @Override
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

    @Override
    @Transactional
    @CacheEvict(value = "activeProposals", allEntries = true)
    public TripProposalDto create(TripProposalRequest request, String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Category> categories = categoryRepository.findAllById(
                request.getCategoryIds() != null ? request.getCategoryIds() : List.of()
        );

        TripProposal proposal = TripProposal.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .location(request.getLocation())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .maxParticipants(request.getMaxParticipants())
                .organizer(organizer)
                .categories(categories)
                .build();

        return convertToDto(tripProposalRepository.save(proposal));
    }

    @Override
    @Transactional
    @CacheEvict(value = "activeProposals", allEntries = true)
    public TripProposalDto update(Long id, TripProposalRequest request, String organizerEmail) {
        TripProposal proposal = tripProposalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Поездка с id " + id + " не найдена"));

        if (!proposal.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new IllegalArgumentException("Нет прав на редактирование этого тура");
        }

        List<Category> categories = categoryRepository.findAllById(
                request.getCategoryIds() != null ? request.getCategoryIds() : List.of()
        );

        proposal.setTitle(request.getTitle());
        proposal.setDescription(request.getDescription());
        proposal.setPrice(request.getPrice());
        proposal.setLocation(request.getLocation());
        proposal.setStartDate(request.getStartDate());
        proposal.setEndDate(request.getEndDate());
        proposal.setMaxParticipants(request.getMaxParticipants());
        proposal.setCategories(categories);
        proposal.setUpdatedAt(LocalDateTime.now());

        return convertToDto(tripProposalRepository.save(proposal));
    }

    @Override
    @Transactional
    @CacheEvict(value = "activeProposals", allEntries = true)
    public void delete(Long id, String organizerEmail) {
        TripProposal proposal = tripProposalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Поездка с id " + id + " не найдена"));

        if (!proposal.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new IllegalArgumentException("Нет прав на удаление этого тура");
        }

        tripProposalRepository.delete(proposal);
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
                .organizerId(entity.getOrganizer().getId())
                .organizerName(entity.getOrganizer().getFullName())
                .categoryNames(entity.getCategories() != null
                        ? entity.getCategories().stream().map(Category::getName).collect(Collectors.toList())
                        : List.of())
                .build();
    }
}
