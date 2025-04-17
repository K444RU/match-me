package com.matchme.srv.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.matchme.srv.model.connection.DatingPool;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class MatchingRepositoryCustomImpl implements MatchingRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<DatingPool> findPotentialMatchesDynamically(DatingPool searchingUser) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<DatingPool> query = cb.createQuery(DatingPool.class);
    Root<DatingPool> dp = query.from(DatingPool.class);

    List<Predicate> predicates = new ArrayList<>();

    if (searchingUser.getLookingForGender() != null) {
      predicates.add(cb.equal(dp.get("myGender"), searchingUser.getLookingForGender()));
    }

    if (searchingUser.getMyGender() != null) {
      predicates.add(cb.equal(dp.get("lookingForGender"), searchingUser.getMyGender()));
    }

    if (searchingUser.getMyAge() != null) {
      predicates.add(cb.lessThanOrEqualTo(dp.get("ageMin"), searchingUser.getMyAge()));
      predicates.add(cb.greaterThanOrEqualTo(dp.get("ageMax"), searchingUser.getMyAge()));
    }

    if (searchingUser.getAgeMin() != null && searchingUser.getAgeMax() != null) {
      predicates.add(cb.between(dp.get("myAge"), searchingUser.getAgeMin(), searchingUser.getAgeMax()));
    }

    if (searchingUser.getProfileId() != null) {
      predicates.add(cb.notEqual(dp.get("profileId"), searchingUser.getProfileId()));
    }

    Set<String> suitableGeohashes = searchingUser.getSuitableGeoHashes();
    System.out.println(suitableGeohashes.toString());

    if (suitableGeohashes != null && !suitableGeohashes.isEmpty()) {
      Predicate[] geohashPredicates = suitableGeohashes.stream()
          .map(prefix -> cb.like(dp.get("myLocation"), prefix + "%"))
          .toArray(Predicate[]::new);

      predicates.add(cb.or(geohashPredicates));
    } else {
      predicates.add(cb.isFalse(cb.literal(true)));
    }

    query.select(dp);
    query.where(predicates.toArray(new Predicate[0]));

    TypedQuery<DatingPool> typedQuery = entityManager.createQuery(query);

    return typedQuery.getResultList();
  }
}
