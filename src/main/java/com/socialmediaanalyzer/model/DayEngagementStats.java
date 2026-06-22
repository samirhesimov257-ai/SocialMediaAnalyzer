package com.socialmediaanalyzer.model;

import java.time.DayOfWeek;

public record DayEngagementStats(
        DayOfWeek day,
        int postCount,
        int totalLikes,
        double averageLikes
) {
}
