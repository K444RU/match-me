package com.matchme.srv.connection;

import java.util.HashSet;
import java.util.Set;

import com.matchme.srv.states.ConnectionState;
import com.matchme.srv.user.User;
import com.matchme.srv.user_messages.UserMessage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
  private Set<UserMessage> userMessages;

  // Somewhere or in an added relationship, also ELO scores should be encoded...



}
