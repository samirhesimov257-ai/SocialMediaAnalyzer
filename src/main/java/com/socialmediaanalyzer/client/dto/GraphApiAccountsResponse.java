package com.socialmediaanalyzer.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GraphApiAccountsResponse(List<GraphApiAccount> data) {
}
