package morning.com.services.user.controller;

import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.dto.PermissionResponse;
import morning.com.services.user.exception.FieldValidationException;
import morning.com.services.user.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PermissionController.class)
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermissionService service;

    @Test
    void listReturnsPermissions() throws Exception {
        PermissionResponse resp = new PermissionResponse(UUID.randomUUID(), "CODE", "SEC", "LBL", Instant.now(), Instant.now());
        Page<PermissionResponse> page = new PageImpl<>(List.of(resp), PageRequest.of(0, 10), 1);
        when(service.search(eq("test"), any())).thenReturn(page);

        mockMvc.perform(get("/user/permission").param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(resp.id().toString()));
    }

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

    @Test
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        when(service.delete(any())).thenReturn(false);
        mockMvc.perform(delete("/user/permission/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.PERMISSION_NOT_FOUND));
    }

    @Test
    void deleteReturnsSuccessWhenFound() throws Exception {
        when(service.delete(any())).thenReturn(true);
        mockMvc.perform(delete("/user/permission/{id}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.SUCCESS));
    }

    @Test
    void bulkCreateReturnsList() throws Exception {
        PermissionResponse resp = new PermissionResponse(UUID.randomUUID(), "C", "S", "L", Instant.now(), Instant.now());
        when(service.addBulk(anyList())).thenReturn(List.of(resp));
        String payload = "[{\"code\":\"C\",\"section\":\"S\",\"label\":\"L\"}]";
        mockMvc.perform(post("/user/permission/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(resp.id().toString()));
    }

    @Test
    void bulkDeleteReturnsSuccess() throws Exception {
        doNothing().when(service).deleteBulk(anyList());
        String payload = "[\"" + UUID.randomUUID() + "\"]";
        mockMvc.perform(delete("/user/permission/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.SUCCESS));
    }
}
