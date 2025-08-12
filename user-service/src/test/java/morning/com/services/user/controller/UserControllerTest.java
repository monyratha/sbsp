package morning.com.services.user.controller;

import morning.com.services.user.model.UserPage;
import morning.com.services.user.model.UserProfile;
import morning.com.services.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserController controller;

    private String authorizationForTenant(String tenant) {
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(("{\"tenantId\":\"" + tenant + "\"}")
                        .getBytes(StandardCharsets.UTF_8));
        return "Bearer " + header + "." + payload + ".";
    }

    @Test
    void addDelegatesToRepository() {
        UserProfile input = new UserProfile("tester", "test@example.com",
                "555-5555", "ACTIVE", "t1");
        UserProfile saved = new UserProfile("tester", "test@example.com",
                "555-5555", "ACTIVE", "t1");
        saved.setId(1L);

        when(repository.add(input)).thenReturn(saved);

        UserProfile result = controller.add(input);

        assertEquals(saved, result);
        verify(repository).add(input);
    }

    @Test
    void findByIdDelegatesToRepository() {
        UserProfile saved = new UserProfile("tester", "test@example.com",
                "555-5555", "ACTIVE", "t1");
        saved.setId(1L);

        when(repository.findById(1L)).thenReturn(saved);

        UserProfile result = controller.findById(1L);

        assertEquals(saved, result);
        verify(repository).findById(1L);
    }

    @Test
    void listDelegatesToRepository() {
        UserPage page = new UserPage(Collections.emptyList(), 0, 0, 20);
        when(repository.search("t1", null, null, 0, 20, "id")).thenReturn(page);

        String auth = authorizationForTenant("t1");
        UserPage result = controller.list(auth, 0, 20, "id", null, null);

        assertSame(page, result);
        verify(repository).search("t1", null, null, 0, 20, "id");
    }
}