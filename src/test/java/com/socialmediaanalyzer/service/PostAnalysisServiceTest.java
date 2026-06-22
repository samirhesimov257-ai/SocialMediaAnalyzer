package com.socialmediaanalyzer.service;

import com.socialmediaanalyzer.client.MetaAccountContext;
import com.socialmediaanalyzer.client.MetaGraphApiClient;
import com.socialmediaanalyzer.config.MetaApiProperties;
import com.socialmediaanalyzer.model.FacebookPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostAnalysisServiceTest {

    @Mock
    private MetaGraphApiClient apiClient;

    private PostAnalysisService service;

    @BeforeEach
    void setUp() {
        MetaApiProperties properties = new MetaApiProperties(
                "https://graph.facebook.com/v25.0",
                "test-token",
                "auto",
                20,
                false
        );
        service = new PostAnalysisService(apiClient, properties);
    }

    @Test
    void analyzePosts_returnsTopThreeAndDayStats() {
        List<FacebookPost> posts = List.of(
                post("1", "A post", LocalDateTime.of(2026, 6, 16, 10, 0), 50, 10, DayOfWeek.MONDAY),
                post("2", "B post", LocalDateTime.of(2026, 6, 17, 10, 0), 30, 5, DayOfWeek.TUESDAY),
                post("3", "C post", LocalDateTime.of(2026, 6, 18, 10, 0), 100, 20, DayOfWeek.WEDNESDAY),
                post("4", "D post", LocalDateTime.of(2026, 6, 19, 10, 0), 10, 2, DayOfWeek.THURSDAY)
        );
        when(apiClient.resolveAccount()).thenReturn(new MetaAccountContext("test-id", "Test Account", "test-token"));
        when(apiClient.fetchRecentPosts()).thenReturn(posts);

        var result = service.analyzePosts();

        assertEquals(3, result.topPostsByEngagement().size());
        assertEquals("3", result.topPostsByEngagement().getFirst().id());
        assertEquals(120, result.topPostsByEngagement().getFirst().engagement());

        assertEquals(DayOfWeek.THURSDAY, result.likesByDayOfWeek().getFirst().day());
        assertEquals(100.0, result.likesByDayOfWeek().getFirst().averageLikes(), 0.001);

        assertTrue(result.summary().contains("4 post"));
    }

    private FacebookPost post(
            String id, String message, LocalDateTime time,
            int likes, int comments, DayOfWeek ignored
    ) {
        return new FacebookPost(id, message, time, likes, comments);
    }
}
