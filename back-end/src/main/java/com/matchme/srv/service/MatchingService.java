package com.matchme.srv.service;

import com.matchme.srv.exception.PotentialMatchesNotFoundException;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.connection.DatingPool;
import com.matchme.srv.repository.MatchingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsible for finding and ranking potential matches for users.
 * Implements a sophisticated matching algorithm that considers
 * multiple factors including:
 * - ELO-based compatibility scoring
 * - Mutual hobbies and interests
 * - Geographic proximity
 * - Age preferences
 * - Gender preferences
 */
@Service
public class MatchingService {

    /**
     * Minimum probability threshold for considering a match.
     * Matches below this threshold are filtered out.
     */
    private static final double MINIMUM_PROBABILITY = 0.3;

    /**
     * Maximum number of matches to return in a single request.
     */
    private static final int DEFAULT_MAX_RESULTS = 10;

    /**
     * Scaling factor for ELO probability calculation.
     * Affects how much score differences impact match probability.
     */
    private static final double SCALING_FACTOR = 1071.0;

    /**
     * Maximum probability threshold for considering a match.
     * Matches above this threshold are filtered out to prevent overmatching.
     */
    private static final double MAXIMUM_PROBABILITY = 0.91;

    private final MatchingRepository matchingRepository;

    @Autowired
    public MatchingService(MatchingRepository matchingRepository) {
        this.matchingRepository = matchingRepository;
    }

    /**
     * Retrieves potential matches for a user based on their preferences and
     * attributes.
     * The matching process involves:
     * 1. Retrieving the user's dating pool entry
     * 2. Finding users that match basic criteria (gender, age, location)
     * 3. Calculating match probability based on ELO scores and mutual interests
     * 4. Filtering and sorting matches by probability
     * 
     * @param userId User ID to find matches for
     * @return Map of user IDs to match probability scores, sorted by probability in
     *         descending order
     * @throws ResourceNotFoundException         if the user is not found
     * @throws PotentialMatchesNotFoundException if no compatible matches are found
     *                                           within acceptable probability range
     */
    public Map<Long, Double> getPossibleMatches(Long userId) {

        // get the users datingPool entry
        DatingPool entry = matchingRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId.toString()));

        // find users that match parameters
        List<DatingPool> possibleMatches = matchingRepository.findUsersThatMatchParameters(entry.getLookingForGender(),
                entry.getMyGender(), entry.getMyAge(), entry.getAgeMin(), entry.getAgeMax(),
                entry.getSuitableGeoHashes(), entry.getMyLocation());

        if (possibleMatches.size() == 0) {
            throw new PotentialMatchesNotFoundException(
                    "Potential matches with selected parameters for user " + userId.toString());
        }
        // Calculate match probability, filter and sort
        Map<Long, Double> bestMatches = possibleMatches.stream()
                .map(pool -> Map.entry(pool.getUserId(),
                        calculateProbability(entry.getActualScore(), entry.getHobbyIds(), pool)))
                .filter(pair -> pair.getValue() > MINIMUM_PROBABILITY)
                .filter(pair -> pair.getValue() < MAXIMUM_PROBABILITY)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(DEFAULT_MAX_RESULTS)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        if (bestMatches.isEmpty()) {
            throw new PotentialMatchesNotFoundException(
                    "Potential matches within acceptable probability range for user " + userId.toString());
        }

        return bestMatches;
    }

    /**
     * Calculates the match probability between two users based on their ELO scores
     * and mutual interests.
     * The calculation combines:
     * - Base probability using ELO formula: P(A) = 1 / (1 + 10^((RB - RA)/1071))
     * - Hobby similarity bonus using logarithmic scaling
     *
     * @param userScore The ELO score of the requesting user
     * @param hobbies   Set of hobby IDs for the requesting user
     * @param entry     Dating pool entry of the potential match
     * @return Calculated match probability between 0.0 and 1.0
     */
    private Double calculateProbability(Integer userScore, Set<Long> hobbies, DatingPool entry) {
        Double probability = 1.0 / (1.0 + Math.pow(10, (userScore - entry.getActualScore()) / SCALING_FACTOR));

        int mutualhobbies = 0;

        for (Long hobby : hobbies) {
            if (entry.getHobbyIds().contains(hobby)) {
                mutualhobbies++;
            }
        }

        if (mutualhobbies != 0) {
            probability += (0.2 * (Math.log(mutualhobbies + 1) / Math.log(hobbies.size() + 1)));
        }

        return probability;
    }
}
