package org.kluge.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
public class SessionInfo {
    private final String id;
    private String userId;
    private final String location;
    private final int x;
    private final int y;

    @JsonCreator
    public SessionInfo(
            @JsonProperty("id") String id,
            @JsonProperty("location") String location,
            @JsonProperty("x") int x,
            @JsonProperty("y") int y
    ) {
        this.id = id;
        this.location = location;
        this.x = x;
        this.y = y;
    }

    public void setUserId(String userId) {
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

