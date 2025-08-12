package morning.com.services.user.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import morning.com.services.user.model.UserPage;
import morning.com.services.user.model.UserProfile;
import org.springframework.stereotype.Repository;

/**
 * In-memory repository for user profiles.
 */
@Repository
public class UserRepository {

    private final List<UserProfile> users = new ArrayList<>();

    public UserProfile add(UserProfile user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }
        users.add(user);
        return user;
    }

    public UserProfile findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Searches users for a tenant with optional query and status filters.
     */
    public UserPage search(String tenantId, String query, String status,
                           int page, int size, String sort) {
        Comparator<UserProfile> comparator = getComparator(sort);

        List<UserProfile> filtered = users.stream()
                .filter(u -> tenantId == null || tenantId.equals(u.getTenantId()))
                .filter(u -> query == null || matchesQuery(u, query))
                .filter(u -> status == null || status.equalsIgnoreCase(u.getStatus()))
                .sorted(comparator)
                .collect(Collectors.toList());

        int total = filtered.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<UserProfile> items = filtered.subList(from, to);

        return new UserPage(items, total, page, size);
    }

    private boolean matchesQuery(UserProfile user, String q) {
        String needle = q.toLowerCase(Locale.ROOT);
        return contains(user.getUsername(), needle)
                || contains(user.getEmail(), needle)
                || contains(user.getPhone(), needle);
    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
    }

    private Comparator<UserProfile> getComparator(String sort) {
        boolean desc = false;
        String field = sort;
        if (sort.startsWith("-") ) {
            desc = true;
            field = sort.substring(1);
        }
        Comparator<UserProfile> comparator;
        switch (field) {
            case "username":
                comparator = Comparator.comparing(UserProfile::getUsername,
                        Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "email":
                comparator = Comparator.comparing(UserProfile::getEmail,
                        Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "phone":
                comparator = Comparator.comparing(UserProfile::getPhone,
                        Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "status":
                comparator = Comparator.comparing(UserProfile::getStatus,
                        Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            default:
                comparator = Comparator.comparing(UserProfile::getId);
        }
        if (desc) {
            comparator = comparator.reversed();
        }
        return comparator;
    }
}
