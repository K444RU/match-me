package com.matchme.srv.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.repository.UserGenderTypeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserGenderTypeService {
    private final UserGenderTypeRepository genderRepository;

    public List<GenderTypeDTO> getAllGenders() {
        return genderRepository.findAll().stream()
                .map(gender -> new GenderTypeDTO(gender.getId(), gender.getName()))
                .toList();
    }
}
