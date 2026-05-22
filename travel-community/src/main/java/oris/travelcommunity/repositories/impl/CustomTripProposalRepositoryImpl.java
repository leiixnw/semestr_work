package oris.travelcommunity.repositories.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import oris.travelcommunity.models.TripProposal;
import oris.travelcommunity.repositories.CustomTripProposalRepository;

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
}