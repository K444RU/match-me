package com.matchme.srv.Activity;

import java.sql.Timestamp;
import java.util.Set;

import com.matchme.srv.EActivity.EActivity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "activity")
public class Activity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  
  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private EActivity activity;

  private Timestamp last_active; //maybe a logic of the two sets below? 

  private Set<Timestamp> log_in_times;

  private Set<Timestamp> log_out_times;

}
