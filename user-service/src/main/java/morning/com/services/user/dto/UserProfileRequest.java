package morning.com.services.user.dto;

import java.util.UUID;

public record UserProfileRequest(UUID userId, String username, String email, String phone, boolean status) {}
