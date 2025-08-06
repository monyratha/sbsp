package morning.com.services.auth.service;

import morning.com.services.auth.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public void register(String username, String password) {
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("User already exists");
        }
        String id = UUID.randomUUID().toString();
        String hash = encoder.encode(password);
        users.put(username, new User(id, username, hash));
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        return user != null && encoder.matches(password, user.getPasswordHash());
    }
}
