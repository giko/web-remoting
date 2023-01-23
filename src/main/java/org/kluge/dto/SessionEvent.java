package org.kluge.dto;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public record SessionEvent(UUID sessionId, String eventName, JsonObject eventData) {
}
