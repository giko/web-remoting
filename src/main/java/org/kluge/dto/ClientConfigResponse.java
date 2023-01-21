package org.kluge.dto;

import java.util.UUID;

public record ClientConfigResponse(String id, UUID userId, int infoPushInterval) {
}
