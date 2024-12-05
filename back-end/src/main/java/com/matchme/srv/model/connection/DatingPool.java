package com.matchme.srv.model.connection;

import java.util.Set;

import com.matchme.srv.model.user.profile.Gender;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dating_pool")
public class DatingPool {
  
  public DatingPool(Long id2, Gender gender, Integer userAge, String geoHash, Integer currentScore2, Set<String> distance2,
      Integer age_min2, Integer age_max2, Integer blindScore) {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long myId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gender_id")
  private Gender myGender;

  private Integer myAge;

  private String myLocation; //Geohash of 6-7 length
  
  private Integer actualScore;

  private Set<String> suitableGeoHashes;

  private Integer age_min;

  private Integer age_max;

  private Integer blindLowerBound; 


}