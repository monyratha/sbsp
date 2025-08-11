package morning.com.services.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import morning.com.services.user.model.User;
import morning.com.services.user.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepository repository;

    @PostMapping
    public User add(@RequestBody User user) {
        LOGGER.info("User add: {}", user);
        return repository.add(user);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Long id) {
        LOGGER.info("User find: id={}", id);
        return repository.findById(id);
    }

    @GetMapping
    public List<User> findAll() {
        LOGGER.info("User find");
        return repository.findAll();
    }
}
