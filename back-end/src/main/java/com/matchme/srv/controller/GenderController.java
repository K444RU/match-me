package com.matchme.srv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchme.srv.model.user.profile.Gender;
import com.matchme.srv.repository.GenderRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/genders")
public class GenderController {
  @Autowired
  private GenderRepository genderRepository;

  @GetMapping()
  public List<Gender> getAllGenders() {
    return genderRepository.findAll();
  }

}