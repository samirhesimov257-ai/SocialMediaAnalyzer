package com.socialmediaanalyzer.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public record FacebookPost(
        String id,
        String message,
        LocalDateTime createdTime,
        int likeCount,
        int commentCount

) {
    public int engagement() {
        return likeCount + commentCount;
    }

    public DayOfWeek dayOfWeek() {
        return createdTime.getDayOfWeek();
    }
}
