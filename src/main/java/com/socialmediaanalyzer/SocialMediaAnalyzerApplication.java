package com.socialmediaanalyzer;

import com.socialmediaanalyzer.config.MetaApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MetaApiProperties.class)
public class SocialMediaAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialMediaAnalyzerApplication.class, args);
    }

}
