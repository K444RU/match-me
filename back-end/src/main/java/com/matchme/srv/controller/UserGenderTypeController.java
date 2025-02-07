package com.matchme.srv.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.service.type.UserGenderTypeService;
import lombok.RequiredArgsConstructor;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genders")
public class UserGenderTypeController {

    private final UserGenderTypeService userGenderTypeService;

    @GetMapping()
    public List<GenderTypeDTO> getAllGenders() {
        return userGenderTypeService.getAll();
    }
}
