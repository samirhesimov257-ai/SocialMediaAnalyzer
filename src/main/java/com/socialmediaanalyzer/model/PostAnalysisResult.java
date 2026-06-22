package com.socialmediaanalyzer.model;

import java.util.List;

public record PostAnalysisResult(
        String accountName,
        String dataSource,
        List<FacebookPost> allPosts,
        List<FacebookPost> topPostsByEngagement,
        List<DayEngagementStats> likesByDayOfWeek,
        String summary
) {
}
