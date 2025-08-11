package morning.com.services.user;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import morning.com.services.user.model.User;
import morning.com.services.user.repository.UserRepository;

@SpringBootApplication
@OpenAPIDefinition(info =
        @Info(title = "User API", version = "1.0", description = "Documentation User API v1.0")
)
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Bean
    UserRepository repository() {
        UserRepository repository = new UserRepository();
        repository.add(new User("John Smith", "john.smith@example.com"));
        repository.add(new User("Jane Doe", "jane.doe@example.com"));
        return repository;
    }
}
