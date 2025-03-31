package com.matchme.srv.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.UserScoreRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing user scores in the matching system.
 * 
 * This service implements an Elo-like rating system to adjust user scores based
 * on connection interactions. When users accept or reject each other, their
 * scores are adjusted accordingly. The score adjustment depends on:
 * - The relative difference between user scores
 * - Whether the interaction was an acceptance or rejection
 * - Scaling and K factors
 * 
 * @see UserScore
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserScoreService {

  /**
   * Scaling factor for the Elo calculation. Controls the significance of score
   * differences.
   * Higher values make score differences less impactful in probability
   * calculations.
   */
  private static final double SCALING_FACTOR = 1071.0;

  /**
   * K-factor determines the maximum possible score change per interaction.
   * Higher values lead to larger score adjustments.
   */
  private static final int K_FACTOR = 12;

  private final UserScoreRepository userScoreRepository;

  /**
   * Updates a sender's score based on the decision of the request receiver.
   * 
   * When a user (decider) accepts or rejects another user (sender), the
   * sender's score is adjusted using an Elo-like algorithm that considers the
   * relative difference between their scores and the outcome of the interaction
   * (accept/reject).
   * 
   * The algorithm:
   * - Calculates the expected accept probability based on score difference
   * - Compares the actual outcome (accept=1, reject=0) with the probability
   * - Adjusts the sender's score proportionally to this difference
   * 
   * Higher-rated users accepting lower-rated users results in smaller increases.
   * Lower-rated users accepting higher-rated users results in larger increases.
   *
   * @param deciderId The ID of the user making the decision
   * @param senderId  The ID of the user who sent the request and whose score will
   *                  be updated
   * @param accept    True if the decider accepted the sender, false if rejected
   * @throws EntityNotFoundException If either user's score entity cannot be found
   */
  @Transactional(readOnly = false)
  public void updateUserScore(Long deciderId, Long senderId, boolean accept) {
    UserScore deciderScore = userScoreRepository.findById(deciderId)
        .orElseThrow(() -> new EntityNotFoundException("ConnectionRequest decider UserScore entity not found."));

    UserScore senderScore = userScoreRepository.findById(senderId)
        .orElseThrow(() -> new EntityNotFoundException("ConnectionRequest sender UserScore entity not found."));

    senderScore
        .setCurrentScore(calculateScoreChange(deciderScore.getCurrentScore(), senderScore.getCurrentScore(), accept));

    userScoreRepository.save(senderScore);
  }

  /**
   * Calculates the new score for the sender
   * 
   * probability = 1 / (1 + 10^((deciderScore - senderScore) / SCALING_FACTOR))
   * scoreChange = K_FACTOR * ((accept ? 1 : 0) - probability)
   * 
   * This results in:
   * - Score increases when accepted by users with equal or higher scores
   * - Larger increases when accepted by users with much higher scores
   * - Score decreases when rejected by any user
   * - Smaller decreases when rejected by users with much lower scores
   *
   * @param deciderScore The current score of the decider user
   * @param senderScore  The current score of the sender user to be updated
   * @param accept       Whether the decider accepted (true) or rejected (false)
   *                     the sender
   * @return The new calculated score for the sender
   */
  private int calculateScoreChange(int deciderScore, int senderScore, boolean accept) {
    Double probability = 1.0 / (1.0 + Math.pow(10, (deciderScore - senderScore) / SCALING_FACTOR));

    return senderScore += (int) Math.floor(K_FACTOR * ((accept ? 1 : 0) - probability));
  }
}
