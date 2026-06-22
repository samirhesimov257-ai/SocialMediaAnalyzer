package com.socialmediaanalyzer.client;

import com.socialmediaanalyzer.client.dto.GraphApiAccount;
import com.socialmediaanalyzer.client.dto.GraphApiAccountsResponse;
import com.socialmediaanalyzer.client.dto.GraphApiMeResponse;
import com.socialmediaanalyzer.client.dto.GraphApiPost;
import com.socialmediaanalyzer.client.dto.GraphApiPostsResponse;
import com.socialmediaanalyzer.config.MetaApiProperties;
import com.socialmediaanalyzer.model.FacebookPost;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MetaGraphApiClient {

    private static final DateTimeFormatter FACEBOOK_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final Pattern ERROR_MESSAGE_PATTERN =
            Pattern.compile("\"message\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");

    private final MetaApiProperties properties;
    private final RestClient restClient;
    private final JsonMapper jsonMapper;

    public MetaGraphApiClient(MetaApiProperties properties) {
        this.properties = properties;
        this.jsonMapper = JsonMapper.builder().build();
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    public MetaAccountContext resolveAccount() {
        if (hasExplicitPageId()) {
            return new MetaAccountContext(properties.pageId(), "Konfiqurasiya edilmiş hesab", properties.accessToken());
        }

        GraphApiAccountsResponse pages = get("/me/accounts?fields=id,name,access_token&access_token={token}",
                GraphApiAccountsResponse.class, properties.accessToken());

        if (pages != null && pages.data() != null && !pages.data().isEmpty()) {
            GraphApiAccount page = pages.data().getFirst();
            String token = page.accessToken() != null ? page.accessToken() : properties.accessToken();
            return new MetaAccountContext(page.id(), page.name(), token);
        }

        GraphApiMeResponse me = get("/me?fields=id,name&access_token={token}",
                GraphApiMeResponse.class, properties.accessToken());

        if (me == null || me.id() == null) {
            throw new MetaGraphApiException("Token ilə hesab məlumatı alına bilmədi.");
        }

        return new MetaAccountContext(me.id(), me.name(), properties.accessToken());
    }

    public List<FacebookPost> fetchRecentPosts() {
        MetaAccountContext account = resolveAccount();
        List<FacebookPost> posts = fetchFromEndpoint(account, "posts");
        if (posts.isEmpty()) {
            posts = fetchFromEndpoint(account, "feed");
        }
        return posts;
    }

    private List<FacebookPost> fetchFromEndpoint(MetaAccountContext account, String edge) {
        String uri = UriComponentsBuilder.fromPath("/{accountId}/" + edge)
                .queryParam("fields", "id,message,created_time,likes.summary(true),comments.summary(true)")
                .queryParam("limit", properties.postLimit())
                .queryParam("access_token", account.accessToken())
                .buildAndExpand(account.accountId())
                .toUriString();

        GraphApiPostsResponse response = get(uri, GraphApiPostsResponse.class);

        if (response == null || response.data() == null) {
            return List.of();
        }

        List<FacebookPost> posts = new ArrayList<>();
        for (GraphApiPost node : response.data()) {
            if (node.createdTime() != null) {
                posts.add(mapPost(node));
            }
        }
        return posts;
    }

    private boolean hasExplicitPageId() {
        return properties.pageId() != null
                && !properties.pageId().isBlank()
                && !"auto".equalsIgnoreCase(properties.pageId());
    }

    private <T> T get(String uri, Class<T> type, Object... uriVariables) {
        try {
            String body = restClient.get()
                    .uri(uri, uriVariables)
                    .retrieve()
                    .body(String.class);
            return jsonMapper.readValue(body, type);
        } catch (RestClientResponseException ex) {
            throw new MetaGraphApiException(parseErrorMessage(ex), ex);
        } catch (RuntimeException ex) {
            throw new MetaGraphApiException("Meta Graph API sorğusu uğursuz oldu: " + ex.getMessage(), ex);
        }
    }

    private String parseErrorMessage(RestClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        Matcher matcher = ERROR_MESSAGE_PATTERN.matcher(body);
        if (matcher.find()) {
            return matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return "Meta Graph API xətası (HTTP " + ex.getStatusCode().value() + "): " + ex.getStatusText();
    }

    private FacebookPost mapPost(GraphApiPost node) {
        String id = node.id() != null ? node.id() : "";
        String message = node.message() != null ? node.message() : "(Mətn yoxdur)";
        LocalDateTime createdTime = LocalDateTime.parse(node.createdTime(), FACEBOOK_DATE_FORMAT);

        int likeCount = node.likes() != null && node.likes().summary() != null
                ? node.likes().summary().totalCount() : 0;
        int commentCount = node.comments() != null && node.comments().summary() != null
                ? node.comments().summary().totalCount() : 0;

        return new FacebookPost(id, message, createdTime, likeCount, commentCount);
    }
}
