package com.matchme.srv.model.connection;

import java.util.HashSet;
import java.util.Set;

import com.matchme.srv.model.message.UserMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.user_score.ConnectionResult;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "connections")
public class Connection {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToMany
  @JoinTable(name = "user_connections", joinColumns = @JoinColumn(name = "connection_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> users = new HashSet<>(); 

  @OneToMany(mappedBy = "connection", cascade = CascadeType.ALL)
  private Set<ConnectionState> connectionStates = new HashSet<>();
  
  @OneToMany(mappedBy = "connection", cascade = CascadeType.ALL)
  private Set<ConnectionResult> connectionResults;
  
  @OneToMany(mappedBy = "connection", cascade = CascadeType.ALL) 
  private Set<UserMessage> userMessages;


  // Somewhere or in an added relationship, also ELO scores should be encoded...



}
