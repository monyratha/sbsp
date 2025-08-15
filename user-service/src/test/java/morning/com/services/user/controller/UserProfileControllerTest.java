package morning.com.services.user.controller;

import morning.com.services.user.dto.ApiResponse;
import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.entity.UserProfile;
import morning.com.services.user.service.RoleService;
import morning.com.services.user.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    UserProfileService service;

    @Mock
    RoleService roleService;

    @InjectMocks
    UserProfileController controller;

    @Test
    void createDelegatesToService() {
        UserProfile input = new UserProfile(null, null, null, null, true, null, null, java.util.Collections.emptySet());
        UserProfile saved = new UserProfile(null, null, null, null, true, null, null, java.util.Collections.emptySet());
        when(service.add(input)).thenReturn(saved);

        ResponseEntity<ApiResponse<UserProfile>> result = controller.create(input);

        assertEquals(201, result.getStatusCodeValue());
        ApiResponse<UserProfile> body = result.getBody();
        assertNotNull(body);
        assertEquals(ApiResponse.SUCCESS, body.code());
        assertEquals(MessageKeys.PROFILE_CREATED, body.messageKey());
        assertSame(saved, body.data());
        verify(service).add(input);
    }

    @Test
    void getReturnsProfileWhenFound() {
        UserProfile saved = new UserProfile(null, null, null, null, true, null, null, java.util.Collections.emptySet());
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.of(saved));

        ResponseEntity<ApiResponse<UserProfile>> response = controller.get(id);

        assertEquals(200, response.getStatusCodeValue());
        ApiResponse<UserProfile> body = response.getBody();
        assertNotNull(body);
        assertEquals(ApiResponse.SUCCESS, body.code());
        assertEquals(MessageKeys.SUCCESS, body.messageKey());
        assertSame(saved, body.data());
        verify(service).findById(id);
    }

    @Test
    void getReturnsNotFoundWhenMissing() {
        UUID missing = UUID.randomUUID();
        when(service.findById(missing)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<UserProfile>> response = controller.get(missing);

        assertEquals(404, response.getStatusCodeValue());
        ApiResponse<UserProfile> body = response.getBody();
        assertNotNull(body);
        assertEquals(ApiResponse.ERROR, body.code());
        assertEquals(MessageKeys.PROFILE_NOT_FOUND, body.messageKey());
        assertNull(body.data());
        verify(service).findById(missing);
    }
}
