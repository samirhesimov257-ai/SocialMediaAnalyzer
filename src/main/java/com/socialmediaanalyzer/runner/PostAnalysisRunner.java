package com.socialmediaanalyzer.runner;

import com.socialmediaanalyzer.config.MetaApiProperties;
import com.socialmediaanalyzer.service.PostAnalysisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class PostAnalysisRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PostAnalysisRunner.class);

    private final PostAnalysisService analysisService;
    private final MetaApiProperties properties;



    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isConfigured()) {
            log.warn("""
                    
                    Meta Graph API konfiqurasiya edilməyib.
                    application-local.properties faylına access-token əlavə edin
                    və ya META_ACCESS_TOKEN environment dəyişənini təyin edin.
                    Hesabat səhifəsi: http://localhost:8080/
                    """);
            return;
        }

        try {
            var result = analysisService.analyzePosts();
            System.out.println(analysisService.formatReportForTerminal(result));
        } catch (Exception ex) {
            log.error("Post analizi uğursuz oldu: {}", ex.getMessage());
        }
    }
}
