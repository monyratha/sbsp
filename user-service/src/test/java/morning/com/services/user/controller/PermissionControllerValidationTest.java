package morning.com.services.user.controller;

import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.exception.FieldValidationException;
import morning.com.services.user.service.PermissionService;
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

@WebMvcTest(PermissionController.class)
class PermissionControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermissionService service;

    @Test
    void createWhenInvalidFieldsReturnsErrors() throws Exception {
        String payload = "{\"code\":\"\",\"section\":\"\",\"label\":\"\"}";
        mockMvc.perform(post("/user/permission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.VALIDATION_ERROR))
                .andExpect(jsonPath("$.data.code").exists())
                .andExpect(jsonPath("$.data.section").exists())
                .andExpect(jsonPath("$.data.label").exists());
    }

    @Test
    void createWhenDuplicateCodeReturnsFieldError() throws Exception {
        when(service.add(any())).thenThrow(new FieldValidationException("code", "already exists"));
        String payload = "{\"code\":\"A\",\"section\":\"S\",\"label\":\"L\"}";
        mockMvc.perform(post("/user/permission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.VALIDATION_ERROR))
                .andExpect(jsonPath("$.data.code").value("already exists"));
    }
}
