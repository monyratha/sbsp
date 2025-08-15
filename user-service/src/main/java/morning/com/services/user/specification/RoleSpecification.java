package morning.com.services.user.specification;

import morning.com.services.user.entity.Role;
import org.springframework.data.jpa.domain.Specification;

public class RoleSpecification {
    public static Specification<Role> searchInAll(String term) {
        return (root, query, cb) -> {
            String like = "%" + term.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("code")), like),
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    public static Specification<Role> codeEquals(String code) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("code")), code.toLowerCase());
    }

    public static Specification<Role> nameEquals(String name) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("name")), name.toLowerCase());
    }
}
