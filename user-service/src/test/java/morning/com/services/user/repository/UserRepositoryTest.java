package morning.com.services.user.repository;

import morning.com.services.user.model.UserProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @Test
    void addUsesProvidedId() {
        UserRepository repository = new UserRepository();
        UserProfile profile = new UserProfile("tester", "test@example.com", "123", "ACTIVE", "t1");
        profile.setId("custom-id");

        repository.add(profile);

        UserProfile found = repository.findById("custom-id");
        assertEquals("custom-id", found.getId());
    }
}
