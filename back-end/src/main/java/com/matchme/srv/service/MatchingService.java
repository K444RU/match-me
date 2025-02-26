package com.matchme.srv.service;

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

@Service
public class MatchingService {

    private static final double MINIMUM_PROBABILITY = 0.3;
    private static final int DEFAULT_MAX_RESULTS = 10;
    private static final int ELO_K_FACTOR = 1071;
    private static final double MAXIMUM_PROBABILITY = 0.7;

    private final MatchingRepository matchingRepository;

    @Autowired
    public MatchingService(MatchingRepository matchingRepository) {
        this.matchingRepository = matchingRepository;
    }

    /**
     * Get possible matches for a user with pagination
     * 
     * @param userId User ID to find matches for
     * @return Map of user IDs to match probability scores
     * @throws RuntimeException if any exceptions occur
     */
    public Map<Long, Double> getPossibleMatches(Long userId) {

        // get the users datingPool entry
        DatingPool entry = matchingRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found in dating pool"));

        // find users that match parameters
        List<DatingPool> possibleMatches = matchingRepository.findUsersThatMatchParameters(entry.getLookingForGender(),
                entry.getMyGender(), entry.getMyAge(), entry.getAgeMin(), entry.getAgeMax(),
                entry.getSuitableGeoHashes(), entry.getMyLocation());

        if (possibleMatches.size() == 0) {
            throw new RuntimeException("No possibible matches found with selected parameters");
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
            throw new RuntimeException("No compatible matches found within acceptable probability range");
        }
        return bestMatches;

    }

    private Double calculateProbability(Integer userScore, Set<Long> hobbies, DatingPool entry) {
        // ELO formula: P(A) = 1 / (1 + 10^((RB - RA)/400))
        Double probability = 1.0 / (1.0 + Math.pow(10, (userScore - entry.getActualScore()) / ELO_K_FACTOR));

        int mutualhobbies = 0;

        for (Long hobby : hobbies) {
            if (entry.getHobbyIds().contains(hobby)) {
                mutualhobbies++;
            }
        }

        if (mutualhobbies != 0) {
            probability = probability + (0.2 * (mutualhobbies / hobbies.size()));
        }

        return probability;
    }
}
