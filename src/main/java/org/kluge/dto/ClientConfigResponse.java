package org.kluge.dto;

import java.util.UUID;

public record ClientConfigResponse(UUID userId, int infoPushInterval) {
}
