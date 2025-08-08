package morning.com.services.auth.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${app.cors.origins:*}")
    private String originsProperty;

    private List<String> allowedOrigins;

    @PostConstruct
    void init() {
        this.allowedOrigins = Arrays.stream(originsProperty.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        log.info("CORS allowed origins: {}", allowedOrigins);

        if (allowedOrigins.size() == 1 && allowedOrigins.contains("*")) {
            throw new IllegalStateException("allowCredentials(true) cannot be used with wildcard '*' for allowed origins");
        }
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.toArray(new String[0]))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization")
                        .allowCredentials(true);
            }
        };
    }
}
