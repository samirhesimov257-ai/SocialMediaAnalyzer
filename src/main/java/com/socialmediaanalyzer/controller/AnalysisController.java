package com.socialmediaanalyzer.controller;

import com.socialmediaanalyzer.client.MetaGraphApiException;
import com.socialmediaanalyzer.model.PostAnalysisResult;
import com.socialmediaanalyzer.service.PostAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final PostAnalysisService analysisService;

    @GetMapping("/analysis")
    public ResponseEntity<?> getAnalysis() {
        try {
            PostAnalysisResult result = analysisService.analyzePosts();
            return ResponseEntity.ok(result);
        } catch (MetaGraphApiException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
