package morning.com.services.user.repository;

import java.util.ArrayList;
import java.util.List;

import morning.com.services.user.model.User;

public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public User add(User user) {
        user.setId((long) (users.size() + 1));
        users.add(user);
        return user;
    }

    public User findById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    public List<User> findAll() {
        return users;
    }
}
