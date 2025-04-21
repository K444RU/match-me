package com.matchme.srv.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.matchme.srv.model.connection.ConnectionType;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.activity.ActivityLogType;
import com.matchme.srv.model.user.profile.ProfileChangeType;
import com.matchme.srv.model.user.profile.user_attributes.AttributeChangeType;
import com.matchme.srv.model.user.profile.user_preferences.PreferenceChangeType;
import com.matchme.srv.repository.ActivityLogTypeRepository;
import com.matchme.srv.repository.AttributeChangeTypeRepository;
import com.matchme.srv.repository.ConnectionTypeRepository;
import com.matchme.srv.repository.PreferenceChangeTypeRepository;
import com.matchme.srv.repository.ProfileChangeTypeRepository;
import com.matchme.srv.repository.UserRoleTypeRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TypesTest {

    @Autowired
    private UserRoleTypeRepository roleRepository;

    @Autowired
    private ConnectionTypeRepository connectionTypeRepository;

    @Autowired
    private ActivityLogTypeRepository activityLogTypeRepository;

    @Autowired
    private AttributeChangeTypeRepository attributeChangeTypeRepository;

    @Autowired
    private PreferenceChangeTypeRepository preferenceChangeTypeRepository;

    @Autowired
    private ProfileChangeTypeRepository profileChangeTypeRepository;

    @Test
    void testRolesInitialization() {
        List<UserRoleType> roles = roleRepository.findAll();
        assertEquals(3, roles.size());
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_USER")));
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_MODERATOR")));
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")));
    }

    @Test
    void testConnectionTypesInitialization() {
        List<ConnectionType> types = connectionTypeRepository.findAll();
        assertEquals(6, types.size());
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("SEEN")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("OPENED_PROFILE")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("JUST_FRIENDS")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("MAYBE_MORE")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("INTERESTED")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("CLOSED")));
    }
    
    @Test
    void testActivityLogTypesInitialization() {
        List<ActivityLogType> types = activityLogTypeRepository.findAll();
        assertEquals(4, types.size());
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("CREATED")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("VERIFIED")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("LOGIN")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("LOGOUT")));
    }

    @Test
    void testAttributeChangeTypesInitialization() {
        List<AttributeChangeType> types = attributeChangeTypeRepository.findAll();
        assertEquals(4, types.size());
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("CREATED")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("GENDER")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("BIRTHDATE")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("LOCATION")));
    }

    @Test
    void testProfileChangeTypesInitialization() {
        List<ProfileChangeType> types = profileChangeTypeRepository.findAll();
        assertEquals(5, types.size());
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("CREATED")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("AGE")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("BIO")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("PHOTO")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("INTERESTS")));
    }

    @Test
    void testPreferenceChangeTypesInitialization() {
        List<PreferenceChangeType> types = preferenceChangeTypeRepository.findAll();
        assertEquals(6, types.size());
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("CREATED")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("GENDER")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("AGE_MIN")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("AGE_MAX")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("DISTANCE")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("TOLERANCE")));
    }

}