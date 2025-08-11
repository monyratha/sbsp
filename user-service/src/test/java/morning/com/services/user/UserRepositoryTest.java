package morning.com.services.user;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.util.Assert;

import morning.com.services.user.model.UserPage;
import morning.com.services.user.model.UserProfile;
import morning.com.services.user.repository.UserRepository;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {

    private static final UserRepository repository = new UserRepository();

    @Test
    @Order(1)
    void testAddUser() {
        UserProfile user = new UserProfile("tester", "test@example.com",
                "555-5555", "ACTIVE", "t1");
        user = repository.add(user);
        Assert.notNull(user, "User is null.");
        Assert.isTrue(user.getId() == 1L, "User bad id.");
    }

    @Test
    @Order(2)
    void testSearch() {
        UserPage page = repository.search("t1", "tester", null, 0, 10, "id");
        Assert.isTrue(page.getTotal() == 1, "Search result size is wrong.");
    }

    @Test
    @Order(3)
    void testFindById() {
        UserProfile user = repository.findById(1L);
        Assert.notNull(user, "User not found.");
        Assert.isTrue(user.getId() == 1L, "User bad id.");
    }
}
