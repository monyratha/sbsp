package morning.com.services.user.controller;

import morning.com.services.user.entity.UserProfile;
import morning.com.services.user.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    UserProfileService service;

    @InjectMocks
    UserProfileController controller;

    @Test
    void createDelegatesToService() {
        UserProfile input = new UserProfile(null, null, null, null, null, null, null, null);
        UserProfile saved = new UserProfile(null, null, null, null, null, null, null, null);
        when(service.add(input)).thenReturn(saved);

        UserProfile result = controller.create(input);

        assertSame(saved, result);
        verify(service).add(input);
    }

    @Test
    void getReturnsProfileWhenFound() {
        UserProfile saved = new UserProfile(null, null, null, null, null, null, null, null);
        when(service.findById("id1")).thenReturn(Optional.of(saved));

        ResponseEntity<UserProfile> response = controller.get("id1");

        assertEquals(200, response.getStatusCodeValue());
        assertSame(saved, response.getBody());
        verify(service).findById("id1");
    }

    @Test
    void getReturnsNotFoundWhenMissing() {
        when(service.findById("missing")).thenReturn(Optional.empty());

        ResponseEntity<UserProfile> response = controller.get("missing");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(service).findById("missing");
    }
}
