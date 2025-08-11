package morning.com.services.user.model;

import java.util.List;

/**
 * Represents a paginated response for user listings.
 */
public class UserPage {

    private List<UserProfile> items;
    private int total;
    private int page;
    private int size;

    public UserPage() {
    }

    public UserPage(List<UserProfile> items, int total, int page, int size) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<UserProfile> getItems() {
        return items;
    }

    public void setItems(List<UserProfile> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

