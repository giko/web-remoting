package org.kluge.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DomainInfo(String domain, int usersCount) {
    @JsonCreator
    public DomainInfo(
            @JsonProperty String domain,
            @JsonProperty int usersCount
    ) {
        this.domain = domain;
        this.usersCount = usersCount;
    }
}
