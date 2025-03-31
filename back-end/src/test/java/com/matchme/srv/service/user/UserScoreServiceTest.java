package com.matchme.srv.service.user;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.UserScoreRepository;

import jakarta.persistence.EntityNotFoundException;

class UserScoreServiceTest {

  @Mock
  private UserScoreRepository userScoreRepository;

  @InjectMocks
  private UserScoreService userScoreService;

  private UserScore deciderScore;
  private UserScore senderScore;
  private final Long deciderId = 1L;
  private final Long senderId = 2L;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    deciderScore = new UserScore();
    deciderScore.setId(deciderId);
    deciderScore.setCurrentScore(1500);

    senderScore = new UserScore();
    senderScore.setId(senderId);
    senderScore.setCurrentScore(1500);
  }

  @Test
  void calculateScoreChange_whenAccepted_shouldIncreaseSenderScore() {
    // Testing private method via reflection
    boolean accept = true;
    int initialScore = senderScore.getCurrentScore();

    // Since calculateScoreChange is private, we'll test it through updateUserScore
    // Set up repository to return our test objects
    when(userScoreRepository.findById(deciderId)).thenReturn(Optional.of(deciderScore));
    when(userScoreRepository.findById(senderId)).thenReturn(Optional.of(senderScore));

    // Call the method
    userScoreService.updateUserScore(deciderId, senderId, accept);

    // Get the new score after update
    int newScore = senderScore.getCurrentScore();

    // Verify score increased (since it was accepted)
    assertTrue(newScore > initialScore);
    verify(userScoreRepository).save(senderScore);
  }

  @Test
  void calculateScoreChange_whenRejected_shouldDecreaseSenderScore() {
    // Testing private method via reflection
    boolean accept = false;
    int initialScore = senderScore.getCurrentScore();

    // Since calculateScoreChange is private, we'll test it through updateUserScore
    // Set up repository to return our test objects
    when(userScoreRepository.findById(deciderId)).thenReturn(Optional.of(deciderScore));
    when(userScoreRepository.findById(senderId)).thenReturn(Optional.of(senderScore));

    // Call the method
    userScoreService.updateUserScore(deciderId, senderId, accept);

    // Get the new score after update
    int newScore = senderScore.getCurrentScore();

    // Verify score increased (since it was accepted)
    assertTrue(newScore < initialScore);
    verify(userScoreRepository).save(senderScore);
  }

  @Test
  void updateUserScore_whenHigherRatedAcceptsLower_smallScoreIncrease() {
    // Higher rated player accepting lower rated player should give smaller increase
    deciderScore.setCurrentScore(1800); // Higher rating

    UserScore deciderScore2 = new UserScore();
    deciderScore2.setId(4L);
    deciderScore2.setCurrentScore(1500);

    UserScore senderScore2 = new UserScore();
    senderScore2.setId(3L);
    senderScore2.setCurrentScore(1500);

    when(userScoreRepository.findById(deciderId)).thenReturn(Optional.of(deciderScore));
    when(userScoreRepository.findById(senderId)).thenReturn(Optional.of(senderScore));
    when(userScoreRepository.findById(deciderScore2.getId())).thenReturn(Optional.of(deciderScore2));
    when(userScoreRepository.findById(senderScore2.getId())).thenReturn(Optional.of(senderScore2));

    userScoreService.updateUserScore(deciderId, senderId, true);
    userScoreService.updateUserScore(deciderScore2.getId(), senderScore2.getId(), true);

    int newScore = senderScore.getCurrentScore();
    int newScore2 = senderScore2.getCurrentScore();

    // Verify the newScore (dissimilar scores) increased more than newScore2
    // (similar scores)
    assertTrue(newScore > newScore2);
    verify(userScoreRepository).save(senderScore);
  }

  @Test
  void updateUserScore_whenIteratedMultipleTimes_scoreIsBiggerThanBefore() {

    deciderScore.setCurrentScore(1800);

    when(userScoreRepository.findById(deciderId)).thenReturn(Optional.of(deciderScore));
    when(userScoreRepository.findById(senderId)).thenReturn(Optional.of(senderScore));

    userScoreService.updateUserScore(deciderId, senderId, true);
    int newScore = senderScore.getCurrentScore();
    userScoreService.updateUserScore(deciderId, senderId, true);
    int newScore2 = senderScore.getCurrentScore();
    userScoreService.updateUserScore(deciderId, senderId, true);
    int newScore3 = senderScore.getCurrentScore();
    userScoreService.updateUserScore(deciderId, senderId, true);
    int newScore4 = senderScore.getCurrentScore();
    userScoreService.updateUserScore(deciderId, senderId, true);
    int newScore5 = senderScore.getCurrentScore();

    // Verify the newScore (dissimilar scores) increased more than newScore2
    // (similar scores)
    assertTrue(newScore < newScore2);
    assertTrue(newScore2 < newScore3);
    assertTrue(newScore3 < newScore4);
    assertTrue(newScore4 < newScore5);
  }

  @Test
  void updateUserScore_whenDeciderNotFound_shouldThrowException() {
    when(userScoreRepository.findById(deciderId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userScoreService.updateUserScore(deciderId, senderId, true));

    verify(userScoreRepository, never()).save(any());
  }

  @Test
  void updateUserScore_whenSenderNotFound_shouldThrowException() {
    when(userScoreRepository.findById(deciderId)).thenReturn(Optional.of(deciderScore));
    when(userScoreRepository.findById(senderId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userScoreService.updateUserScore(deciderId, senderId, true));

    verify(userScoreRepository, never()).save(any());
  }
}
