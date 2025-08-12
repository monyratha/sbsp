package morning.com.services.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with the user-service.
 */
@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/user")
    void add(@RequestBody UserProfile profile);
}
