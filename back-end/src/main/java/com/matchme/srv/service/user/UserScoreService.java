package com.matchme.srv.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.UserScoreRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserScoreService {

  private static final double SCALING_FACTOR = 1071.0;
  private static final int K_FACTOR = 12;

  private final UserScoreRepository userScoreRepository;

  @Transactional(readOnly = false)
  public void updateUserScore(Long DeciderId, Long senderId, boolean accept) {

    UserScore deciderScore = userScoreRepository.findById(DeciderId)
        .orElseThrow(() -> new EntityNotFoundException("ConnectionRequest decider UserScore entity not found."));

    UserScore senderScore = userScoreRepository.findById(senderId)
        .orElseThrow(() -> new EntityNotFoundException("ConnectionRequest sender UserScore entity not found."));

    senderScore
        .setCurrentScore(calculateScoreChange(deciderScore.getCurrentScore(), senderScore.getCurrentScore(), accept));

    userScoreRepository.save(senderScore);
  }

  private int calculateScoreChange(int deciderScore, int senderScore, boolean accept) {
    Double probability = 1.0 / (1.0 + Math.pow(10, (deciderScore - senderScore) / SCALING_FACTOR));

    return senderScore += (int) Math.floor(K_FACTOR * ((accept ? 1 : 0) - probability));
  }
}
