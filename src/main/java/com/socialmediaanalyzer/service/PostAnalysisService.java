package com.socialmediaanalyzer.service;

import com.socialmediaanalyzer.client.MetaAccountContext;
import com.socialmediaanalyzer.client.MetaGraphApiClient;
import com.socialmediaanalyzer.config.MetaApiProperties;
import com.socialmediaanalyzer.model.DayEngagementStats;
import com.socialmediaanalyzer.model.FacebookPost;
import com.socialmediaanalyzer.model.PostAnalysisResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostAnalysisService {

    private static final Locale AZ_LOCALE = Locale.forLanguageTag("az");

    private final MetaGraphApiClient apiClient;
    private final MetaApiProperties properties;

    public PostAnalysisResult analyzePosts() {
        if (!properties.isConfigured()) {
            throw new IllegalStateException(
                    "Meta Graph API konfiqurasiya edilməyib. META_ACCESS_TOKEN və ya application-local.properties faylında token təyin edin.");
        }

        MetaAccountContext account = apiClient.resolveAccount();
        List<FacebookPost> posts = apiClient.fetchRecentPosts();
        String dataSource = "live";

        if (posts.isEmpty()) {
            if (!properties.useDemoOnEmpty()) {
                throw new IllegalStateException(String.format(
                        "Hesab '%s' (%s) üçün post tapılmadı. "
                                + "Facebook səhifə token-i və pages_read_engagement icazəsi lazımdır; "
                                + "şəxsi profil postları Graph API ilə məhdudlaşdırılıb.",
                        account.accountName(), account.accountId()));
            }
            posts = DemoPostDataProvider.createSamplePosts();
            dataSource = "demo";
        }

        List<FacebookPost> topPosts = posts.stream()
                .sorted(Comparator.comparingInt(FacebookPost::engagement).reversed())
                .limit(3)
                .toList();

        List<DayEngagementStats> likesByDay = calculateLikesByDayOfWeek(posts);
        String summary = buildSummary(account, posts, topPosts, likesByDay, dataSource);

        return new PostAnalysisResult(account.accountName(), dataSource, posts, topPosts, likesByDay, summary);
    }

    private List<DayEngagementStats> calculateLikesByDayOfWeek(List<FacebookPost> posts) {
        Map<DayOfWeek, int[]> stats = new EnumMap<>(DayOfWeek.class);

        for (FacebookPost post : posts) {
            stats.computeIfAbsent(post.dayOfWeek(), day -> new int[2]);
            int[] counts = stats.get(post.dayOfWeek());
            counts[0]++;
            counts[1] += post.likeCount();
        }

        return stats.entrySet().stream()
                .map(entry -> {
                    int postCount = entry.getValue()[0];
                    int totalLikes = entry.getValue()[1];
                    double averageLikes = postCount == 0 ? 0 : (double) totalLikes / postCount;
                    return new DayEngagementStats(entry.getKey(), postCount, totalLikes, averageLikes);
                })
                .sorted(Comparator.comparingDouble(DayEngagementStats::averageLikes).reversed())
                .toList();
    }

    private String buildSummary(
            MetaAccountContext account,
            List<FacebookPost> posts,
            List<FacebookPost> topPosts,
            List<DayEngagementStats> likesByDay,
            String dataSource
    ) {
        int totalEngagement = posts.stream().mapToInt(FacebookPost::engagement).sum();
        double avgEngagement = (double) totalEngagement / posts.size();

        int totalLikes = posts.stream().mapToInt(FacebookPost::likeCount).sum();
        int totalComments = posts.stream().mapToInt(FacebookPost::commentCount).sum();

        DayEngagementStats bestDay = likesByDay.getFirst();
        String bestDayName = bestDay.day().getDisplayName(TextStyle.FULL, AZ_LOCALE);

        String topPostPreview = topPosts.isEmpty()
                ? "—"
                : truncate(topPosts.getFirst().message(), 60);

        String demoNote = "demo".equals(dataSource)
                ? String.format(
                " [QEYD: '%s' hesabından API ilə post alınmadı; analiz demo məlumatlarla göstərilir.]",
                account.accountName())
                : "";

        return String.format(
                "Son %d post üzrə orta engagement: %.1f (like: %d, şərh: %d). "
                        + "Ən yüksək engagement: %d (post: \"%s\"). "
                        + "Like-lar üzrə ən yaxşı gün: %s (orta %.1f like/post, %d post).%s",
                posts.size(),
                avgEngagement,
                totalLikes,
                totalComments,
                topPosts.isEmpty() ? 0 : topPosts.getFirst().engagement(),
                topPostPreview,
                bestDayName,
                bestDay.averageLikes(),
                bestDay.postCount(),
                demoNote
        );
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    public String formatReportForTerminal(PostAnalysisResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("=".repeat(70)).append("\n");
        sb.append("  META GRAPH API — POST PERFORMANS ANALİZİ\n");
        sb.append("  Hesab: ").append(result.accountName());
        if ("demo".equals(result.dataSource())) {
            sb.append("  [Demo məlumat — API-dən post gəlmədi]");
        }
        sb.append("\n");
        sb.append("=".repeat(70)).append("\n\n");

        sb.append("--- BÜTÜN POSTLAR (").append(result.allPosts().size()).append(") ---\n");
        for (int i = 0; i < result.allPosts().size(); i++) {
            FacebookPost post = result.allPosts().get(i);
            sb.append(String.format(
                    "%2d. [%s] Like: %d | Şərh: %d | Engagement: %d%n   %s%n",
                    i + 1,
                    post.createdTime().toLocalDate(),
                    post.likeCount(),
                    post.commentCount(),
                    post.engagement(),
                    truncate(post.message(), 80)
            ));
        }

        sb.append("\n--- TOP 3 POST (ENGAGEMENT) ---\n");
        for (int i = 0; i < result.topPostsByEngagement().size(); i++) {
            FacebookPost post = result.topPostsByEngagement().get(i);
            sb.append(String.format(
                    "%d. Engagement: %d (Like: %d, Şərh: %d) — %s%n   %s%n",
                    i + 1,
                    post.engagement(),
                    post.likeCount(),
                    post.commentCount(),
                    post.createdTime().toLocalDate(),
                    truncate(post.message(), 80)
            ));
        }

        sb.append("\n--- GÜNLƏR ÜZRƏ LIKE STATİSTİKASI ---\n");
        for (DayEngagementStats day : result.likesByDayOfWeek()) {
            String dayName = day.day().getDisplayName(TextStyle.FULL, AZ_LOCALE);
            sb.append(String.format(
                    "  %-12s | Post: %2d | Cəmi like: %4d | Orta like/post: %.1f%n",
                    dayName,
                    day.postCount(),
                    day.totalLikes(),
                    day.averageLikes()
            ));
        }

        sb.append("\n--- QISA NƏTİCƏ ---\n");
        sb.append("  ").append(result.summary()).append("\n");
        sb.append("\n").append("=".repeat(70)).append("\n");

        return sb.toString();
    }
}
