package morning.com.services.user.controller;

import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.dto.RoleResponse;
import morning.com.services.user.exception.FieldValidationException;
import morning.com.services.user.exception.GlobalExceptionHandler;
import morning.com.services.user.service.RoleService;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void listReturnsRoles() throws Exception {
        RoleResponse resp = new RoleResponse(UUID.randomUUID(), "CODE", "NAME", "DESC");
        Page<RoleResponse> page = new PageImpl<>(List.of(resp), PageRequest.of(0, 10), 1);
        when(service.search(eq("test"), eq("CODE"), eq("NAME"), any())).thenReturn(page);

        mockMvc.perform(get("/user/api/role")
                        .param("search", "test")
                        .param("code", "CODE")
                        .param("name", "NAME"))
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
        Page<RoleResponse> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(service.search(isNull(), isNull(), isNull(), any())).thenReturn(page);

        mockMvc.perform(get("/user/api/role"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).search(isNull(), isNull(), isNull(), captor.capture());
        Sort.Order order = captor.getValue().getSort().getOrderFor("createdAt");
        assertNotNull(order);
        assertTrue(order.isDescending());
    }

    @Test
    void createWhenInvalidFieldsReturnsErrors() throws Exception {
        String payload = "{\"code\":\"\",\"name\":\"\",\"description\":\"\"}";
        mockMvc.perform(post("/user/api/role")
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
        mockMvc.perform(post("/user/api/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messageKey").value(MessageKeys.VALIDATION_ERROR))
                .andExpect(jsonPath("$.data.name").value("already exists"));
    }
}
