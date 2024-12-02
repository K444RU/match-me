package com.matchme.srv.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matchme.srv.model.connection.DatingPool;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.ConnectionRepository;
import com.matchme.srv.repository.UserAttributesRepository;
import com.matchme.srv.repository.UserPreferencesRepository;
import com.matchme.srv.repository.UserScoreRepository;

@Service
public class MatchingService {

  private final ConnectionRepository matchingRepository;
  private final UserPreferencesRepository preferencesRepository;
  private final UserAttributesRepository attributesRepository;
  private final UserScoreRepository scoreRepository;

  @Autowired
  public MatchingService(ConnectionRepository matchingRepository, UserPreferencesRepository userPreferencesRepository, UserAttributesRepository userAttributesRepository, UserScoreRepository userScoreRepository) {
    this.matchingRepository = matchingRepository;
    this.preferencesRepository = userPreferencesRepository;
    this.attributesRepository = userAttributesRepository;
    this.scoreRepository = userScoreRepository;
  }

  public Long getMatch(Long userId) {

    // get the users preferences for a match
    UserPreferences preferences = getPreferences(userId); // gender, age_min, age_max, distance, blind, probabilityTolerance

    // get the users attributes to reverse-check
    UserAttributes attributes = getAttributes(userId); // myGender, myBirthDate, location

    // get the users scores 
    UserScore scores = getUserScores(userId); // currentScore, vibeProbability, currentBlind

    // parse users birthdate to correct age.
    Integer userAge = parseBirthDateToString(attributes.getBirthDate()); // birthdate -> age

    // TODO: Make the location a geohash
    String geoHash = getGeoHash(attributes.getLocation()); // location -> geoHash

    // TODO: find suitable geohash codes 
    Set<String> suitableGeoHashes = getSuitableGeoHashes(geoHash, preferences.getDistance());

    // Get users current score
    Integer actualScore = calculateUsersOwnScore(scores.getCurrentScore(), scores.getVibeProbability());

    // Calculate minimum score the other user must have
    Integer blindScore = calculateBlindLowerBound(scores.getCurrentBlind(), preferences.getProbabilityTolerance());

    //TODO: make a search query in the database 
    Long bestMatchId = getDatingPoolBestMatch();
    
    // TODO: Go over the entity creation and check auto-generated constructor stub in DatingPool
    if (bestMatchId == null) {
      DatingPool userRow = new DatingPool(attributes.getId(), attributes.getGender(), userAge, geoHash, actualScore, suitableGeoHashes, preferences.getAge_min(), preferences.getAge_max(), blindScore);
      matchingRepository.save(userRow);
      return null;
    } else {
      return bestMatchId;
    }
  }


  public UserPreferences getPreferences(Long userId) {
    
    Optional<UserPreferences> userPreferences = preferencesRepository.findById(userId);
    if (!userPreferences.isPresent()) {
      // TODO: What happens if user does not have preferences set?
    }

    return userPreferences.get();
  }

  public UserAttributes getAttributes(Long userId) {

    Optional<UserAttributes> userAttributes = attributesRepository.findById(userId);
    
    if (!userAttributes.isPresent()) {
      // TODO: What happens if user does not have attributes set?
    }

    return userAttributes.get();

  }

  public UserScore getUserScores(Long userId) {
    Optional<UserScore> userScore = scoreRepository.findById(userId);
    if (!userScore.isPresent()) {
      // TODO: What happens if user does not have a score entity?
    }

    return userScore.get();
  }

  public Integer parseBirthDateToString(LocalDate birthDate) {
    LocalDate currentDate = LocalDate.now();
    Integer age = currentDate.getYear() - birthDate.getYear();

    if (currentDate.getMonthValue() > birthDate.getMonthValue() || 
    currentDate.getMonth() == birthDate.getMonth() && currentDate.getDayOfMonth() > birthDate.getDayOfMonth()) {
      age--;
    }

    return age;
  }
  
  public int calculateUsersOwnScore(int score, double probability) {

    int realScore = (int) Math.round(score * probability);
    
    return realScore;
  }
  
  // TODO: Fix the logic... 
  public int calculateBlindLowerBound(int currentBlind, double probability) {
    
    double blind = Math.log10((Math.pow(10, currentBlind) - Math.pow(10, currentBlind) * probability) / probability);

    return (int) Math.round(blind);
  }

  public String getGeoHash(List<Double> location) {
    return "geoHash";
  }

  public Set<String> getSuitableGeoHashes(String centralGeoHash, Integer distance) {
    Set<String> geoHashes = new HashSet<>();
    return geoHashes;
  }

  // TODO: Complete this logic to find matches in DatingPool
  public Long getDatingPoolBestMatch() {
    DatingPool[] allUsers = new DatingPool[10];
    return allUsers[0].getMyId();
  }

}
