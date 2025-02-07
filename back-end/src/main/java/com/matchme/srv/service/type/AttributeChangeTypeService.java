package com.matchme.srv.service.type;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.profile.user_attributes.AttributeChangeType;
import com.matchme.srv.repository.AttributeChangeTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttributeChangeTypeService {

  private final AttributeChangeTypeRepository attributeChangeTypeRepository;

  public AttributeChangeType getByName(String name) {
    return attributeChangeTypeRepository
        .findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("Attribute"));
  }
}
