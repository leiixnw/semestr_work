package oris.travelcommunity.repositories.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import oris.travelcommunity.models.TripProposal;
import oris.travelcommunity.repositories.CustomTripProposalRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CustomTripProposalRepositoryImpl implements CustomTripProposalRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TripProposal> findProposalsWithHighRatingApplicants() {
        String jpql = "SELECT tp FROM TripProposal tp WHERE tp.id IN (" +
                "  SELECT ta.proposal.id FROM TripApplication ta WHERE ta.traveler.id IN (" +
                "    SELECT u.id FROM User u WHERE u.rating > 4.5" +
                "  )" +
                ")";

        return entityManager.createQuery(jpql, TripProposal.class).getResultList();
    }

    @Override
    public List<TripProposal> searchProposalsCustom(String location, String status) {
        String jpql = "SELECT tp FROM TripProposal tp WHERE tp.location = :location AND tp.status = :status";
        TypedQuery<TripProposal> query = entityManager.createQuery(jpql, TripProposal.class);
        query.setParameter("location", location);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<TripProposal> findProposalsByCriteria(String location, BigDecimal minPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TripProposal> query = cb.createQuery(TripProposal.class);
        Root<TripProposal> root = query.from(TripProposal.class);

        List<Predicate> predicates = new ArrayList<>();

        if (location != null && !location.isEmpty()) {
            predicates.add(cb.equal(root.get("location"), location));
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
    }

}