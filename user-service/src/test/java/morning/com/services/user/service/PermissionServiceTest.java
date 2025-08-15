package morning.com.services.user.service;

import morning.com.services.user.mapper.PermissionMapper;
import morning.com.services.user.repository.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository repository;

    @Mock
    private PermissionMapper mapper;

    @InjectMocks
    private PermissionService service;

    @Test
    void searchWithQueryUsesSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        service.search("test", pageable);

        verify(repository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchWithoutQueryUsesFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(Page.empty());

        service.search(null, pageable);

        verify(repository).findAll(pageable);
    }
}

