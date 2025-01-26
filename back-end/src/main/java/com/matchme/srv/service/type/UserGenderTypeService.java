package com.matchme.srv.service.type;

import java.util.List;
import org.springframework.stereotype.Service;
import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.repository.UserGenderTypeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserGenderTypeService {
    private final UserGenderTypeRepository genderRepository;

    public List<GenderTypeDTO> getAll() {
        return genderRepository.findAll().stream()
                .map(gender -> new GenderTypeDTO(gender.getId(), gender.getName()))
                .toList();
    }

    public UserGenderType getById(Long genderId) {
        return genderRepository.findById(genderId)
                .orElseThrow(() -> new ResourceNotFoundException("Gender"));
    }

    public UserGenderType getByName(String name) {
        return genderRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Gender"));
    }
}
