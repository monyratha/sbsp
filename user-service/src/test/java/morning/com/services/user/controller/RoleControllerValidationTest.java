package morning.com.services.user.controller;

import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.exception.FieldValidationException;
import morning.com.services.user.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
class RoleControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService service;

    @Test
    void createWhenInvalidFieldsReturnsErrors() throws Exception {
        String payload = "{\"name\":\"\",\"description\":\"\"}";
        mockMvc.perform(post("/user/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.VALIDATION_ERROR))
                .andExpect(jsonPath("$.data.name").exists());
    }

    @Test
    void createWhenDuplicateNameReturnsFieldError() throws Exception {
        when(service.add(any())).thenThrow(new FieldValidationException("name", "already exists"));
        String payload = "{\"name\":\"admin\",\"description\":\"\"}";
        mockMvc.perform(post("/user/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.VALIDATION_ERROR))
                .andExpect(jsonPath("$.data.name").value("already exists"));
    }
}
