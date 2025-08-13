package morning.com.services.user.dto;

import java.util.List;

public record BulkRequest(List<BulkOp> operations) {}
