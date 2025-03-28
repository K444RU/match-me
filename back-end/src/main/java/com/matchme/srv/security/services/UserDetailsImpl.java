package com.matchme.srv.security.services;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matchme.srv.model.user.User;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String email;
    
    @JsonIgnore
    private String password;

    private Set<GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String password,
            Set<GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }
    public static UserDetailsImpl build(User user, String password) {

      Set<GrantedAuthority> authorities = user.getRoles()
          .stream()
          .map(role -> new SimpleGrantedAuthority(role.getName()))
          .collect(Collectors.toSet());
    
      return new UserDetailsImpl(
          user.getId(), 
          user.getEmail(),
          password, 
          authorities);
      }
    
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
          return authorities;
      }
    
      public Long getId() {
        return id;
      }
    
      public String getEmail() {
        return email;
      }
    
      @Override
      public String getUsername() {
          return email;
      }

      @Override
      public String getPassword() {
        return password;
      }
    
      @Override
      public boolean isAccountNonExpired() {
        return true;
      }
    
      @Override
      public boolean isAccountNonLocked() {
        return true;
      }
    
      @Override
      public boolean isCredentialsNonExpired() {
        return true;
      }
    
      @Override
      public boolean isEnabled() {
        return true;
      }
    
      @Override
      public boolean equals(Object o) {
        if (this == o)
          return true;
        if (o == null || getClass() != o.getClass())
          return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
      }
    }