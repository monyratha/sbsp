package morning.com.services.user.specification;

import morning.com.services.user.entity.Permission;
import org.springframework.data.jpa.domain.Specification;

public class PermissionSpecification {
    public static Specification<Permission> searchInAll(String term) {
        return (root, query, cb) -> {
            String like = "%" + term.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("code")), like),
                    cb.like(cb.lower(root.get("section")), like),
                    cb.like(cb.lower(root.get("label")), like)
            );
        };
    }

    public static Specification<Permission> sectionEquals(String section) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("section")), section.toLowerCase());
    }

    public static Specification<Permission> codeEquals(String code) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("code")), code.toLowerCase());
    }
}
