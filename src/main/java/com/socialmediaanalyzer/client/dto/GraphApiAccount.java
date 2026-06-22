package com.socialmediaanalyzer.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GraphApiAccount(
        String id,
        String name,
        @JsonProperty("access_token") String accessToken
) {
}
