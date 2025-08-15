package morning.com.services.user.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Minimal wrapper for paginated responses.
 */
public record PageResult<T>(
        List<T> items,
        int page,
        int size,
        long total,
        int totalPages,
        boolean hasNext) {

    public static <T> PageResult<T> from(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}
