package com.socialmediaanalyzer.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GraphApiMeResponse(String id, String name) {
}
