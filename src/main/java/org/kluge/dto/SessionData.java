package org.kluge.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.UUID;

@Data
@Setter(AccessLevel.PRIVATE)
public class SessionData {
    private UUID id;
    private UUID userId;
    private final String location;
    private final int x;
    private final int y;

    @JsonCreator
    public SessionData(
            @JsonProperty("location") String location,
            @JsonProperty("x") int x,
            @JsonProperty("y") int y
    ) {
        this.location = location;
        this.x = x;
        this.y = y;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getDomain() {
        if (location == null) {
            return null;
        }
        String[] parts = location.split("/");
        if (parts.length < 3) {
            return null;
        }
        return parts[2];
    }
}

