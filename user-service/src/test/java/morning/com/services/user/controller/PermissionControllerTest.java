package morning.com.services.user.controller;

import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.dto.PermissionResponse;
import morning.com.services.user.exception.FieldValidationException;
import morning.com.services.user.exception.GlobalExceptionHandler;
import morning.com.services.user.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PermissionService service;

    @InjectMocks
    private PermissionController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listReturnsPermissions() throws Exception {
        PermissionResponse resp = new PermissionResponse(UUID.randomUUID(), "CODE", "SEC", "LBL", Instant.now(), Instant.now());
        Page<PermissionResponse> page = new PageImpl<>(List.of(resp), PageRequest.of(0, 10), 1);
        when(service.search(eq("test"), eq("SEC"), eq("CODE"), any())).thenReturn(page);

        mockMvc.perform(get("/user/api/permission")
                        .param("search", "test")
                        .param("section", "SEC")
                        .param("code", "CODE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].id").value(resp.id().toString()))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.hasNext").value(false));
    }

    @Test
    void listUsesCreatedAtDescByDefault() throws Exception {
        Page<PermissionResponse> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(service.search(isNull(), isNull(), isNull(), any())).thenReturn(page);

        mockMvc.perform(get("/user/api/permission"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).search(isNull(), isNull(), isNull(), captor.capture());
        Sort.Order order = captor.getValue().getSort().getOrderFor("createdAt");
        assertNotNull(order);
        assertTrue(order.isDescending());
    }

    @Test
    void createWhenInvalidFieldsReturnsErrors() throws Exception {
        String payload = "{\"code\":\"\",\"section\":\"\",\"label\":\"\"}";
        mockMvc.perform(post("/user/api/permission")
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
        mockMvc.perform(post("/user/api/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.VALIDATION_ERROR))
                .andExpect(jsonPath("$.data.code").value("already exists"));
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        when(service.delete(any())).thenReturn(false);
        mockMvc.perform(delete("/user/api/permission/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.PERMISSION_NOT_FOUND));
    }

    @Test
    void deleteReturnsSuccessWhenFound() throws Exception {
        when(service.delete(any())).thenReturn(true);
        mockMvc.perform(delete("/user/api/permission/{id}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.SUCCESS));
    }

    @Test
    void bulkCreateReturnsList() throws Exception {
        PermissionResponse resp = new PermissionResponse(UUID.randomUUID(), "C", "S", "L", Instant.now(), Instant.now());
        when(service.addBulk(anyList())).thenReturn(List.of(resp));
        String payload = "[{\"code\":\"C\",\"section\":\"S\",\"label\":\"L\"}]";
        mockMvc.perform(post("/user/api/permission/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(resp.id().toString()));
    }

    @Test
    void bulkDeleteReturnsSuccess() throws Exception {
        doNothing().when(service).deleteBulk(anyList());
        String payload = "[\"" + UUID.randomUUID() + "\"]";
        mockMvc.perform(delete("/user/api/permission/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.SUCCESS));
    }
}
