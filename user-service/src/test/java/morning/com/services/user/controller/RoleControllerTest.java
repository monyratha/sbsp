package morning.com.services.user.controller;

import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.exception.FieldValidationException;
import morning.com.services.user.exception.GlobalExceptionHandler;
import morning.com.services.user.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoleService service;

    @InjectMocks
    private RoleController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createWhenInvalidFieldsReturnsErrors() throws Exception {
        String payload = "{\"code\":\"\",\"name\":\"\",\"description\":\"\"}";
        mockMvc.perform(post("/user/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.VALIDATION_ERROR))
                .andExpect(jsonPath("$.data.code").exists());
    }

    @Test
    void createWhenDuplicateNameReturnsFieldError() throws Exception {
        when(service.add(any())).thenThrow(new FieldValidationException("name", "already exists"));
        String payload = "{\"code\":\"admin\",\"name\":\"admin\",\"description\":\"\"}";
        mockMvc.perform(post("/user/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.VALIDATION_ERROR))
                .andExpect(jsonPath("$.data.name").value("already exists"));
    }
}
