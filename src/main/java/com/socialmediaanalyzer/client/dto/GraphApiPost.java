package com.socialmediaanalyzer.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GraphApiPost(
        String id,
        String message,
        @JsonProperty("created_time") String createdTime,
        GraphApiSummary likes,
        GraphApiSummary comments
) {
}
