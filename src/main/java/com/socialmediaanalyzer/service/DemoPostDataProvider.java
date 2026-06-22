package com.socialmediaanalyzer.service;

import com.socialmediaanalyzer.model.FacebookPost;

import java.time.LocalDateTime;
import java.util.List;

final class DemoPostDataProvider {

    private DemoPostDataProvider() {
    }

    static List<FacebookPost> createSamplePosts() {
        return List.of(
                post("demo-1", "Yeni layihəmiz haqqında qısa yeniləmə paylaşırıq.", LocalDateTime.of(2026, 6, 2, 10, 0), 45, 8),
                post("demo-2", "Komanda ilə birgə iş günü — çox məhsuldar keçdi.", LocalDateTime.of(2026, 6, 3, 14, 30), 62, 12),
                post("demo-3", "Həftəsonu tədbirimizdən fotoşəkillər.", LocalDateTime.of(2026, 6, 6, 18, 0), 120, 25),
                post("demo-4", "Yeni məhsulumuz artıq satışdadır!", LocalDateTime.of(2026, 6, 7, 9, 15), 88, 19),
                post("demo-5", "Müştəri rəyləri bizim üçün çox dəyərlidir.", LocalDateTime.of(2026, 6, 9, 11, 0), 34, 5),
                post("demo-6", "Canlı yayım: suallarınızı gözləyirik.", LocalDateTime.of(2026, 6, 10, 20, 0), 210, 47),
                post("demo-7", "Bazar ertəsi motivasiya postu — yeni həftəyə hazırıq.", LocalDateTime.of(2026, 6, 13, 8, 30), 55, 9),
                post("demo-8", "Behind the scenes: ofis gündəliyimiz.", LocalDateTime.of(2026, 6, 14, 16, 45), 73, 14),
                post("demo-9", "Endirim kampaniyası yalnız bu həftə!", LocalDateTime.of(2026, 6, 16, 12, 0), 156, 31),
                post("demo-10", "Təlim proqramımıza qeydiyyat açıqdır.", LocalDateTime.of(2026, 6, 17, 10, 20), 41, 7),
                post("demo-11", "Komanda üzvlərimizlə tanış olun.", LocalDateTime.of(2026, 6, 18, 15, 0), 98, 22),
                post("demo-12", "Tez-tez verilən suallar — 2-ci hissə.", LocalDateTime.of(2026, 6, 19, 9, 40), 29, 4),
                post("demo-13", "Cümə günü xülasəsi: həftənin nailiyyətləri.", LocalDateTime.of(2026, 6, 20, 17, 30), 67, 11),
                post("demo-14", "Yeni bloq yazımızı oxuyun.", LocalDateTime.of(2026, 6, 21, 13, 10), 52, 8),
                post("demo-15", "İşə qəbul elanı — bizə qoşulun.", LocalDateTime.of(2026, 6, 22, 10, 0), 84, 16),
                post("demo-16", "Məhsul demo videosu.", LocalDateTime.of(2026, 5, 28, 19, 0), 140, 28),
                post("demo-17", "Sorğu: növbəti mövzunuz nə olsun?", LocalDateTime.of(2026, 5, 30, 11, 30), 95, 38),
                post("demo-18", "Təşəkkür edirik — 1000 izləyici!", LocalDateTime.of(2026, 5, 31, 21, 0), 175, 42),
                post("demo-19", "Ofis yenilikləri və yeni iş saatları.", LocalDateTime.of(2026, 6, 1, 9, 0), 38, 6),
                post("demo-20", "Qısa video: günün ipucları.", LocalDateTime.of(2026, 6, 4, 14, 0), 112, 20)
        );
    }

    private static FacebookPost post(String id, String message, LocalDateTime createdTime, int likes, int comments) {
        return new FacebookPost(id, message, createdTime, likes, comments);
    }
}
