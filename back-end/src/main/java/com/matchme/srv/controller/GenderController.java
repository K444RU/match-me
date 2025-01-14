package com.matchme.srv.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.repository.UserGenderTypeRepository;
import lombok.RequiredArgsConstructor;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genders")
public class GenderController {

    private final UserGenderTypeRepository genderRepository;

    @GetMapping()
    public List<GenderTypeDTO> getAllGenders() {
        return genderRepository.findAll().stream()
                .map(gender -> new GenderTypeDTO(gender.getId(), gender.getName()))
                .collect(Collectors.toList());
    }
}
