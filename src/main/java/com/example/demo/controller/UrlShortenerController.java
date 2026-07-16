package com.example.demo.controller;

import com.example.demo.model.ShortLink;
import com.example.demo.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    public record ShortenRequest(String originalUrl) {}

    @PostMapping("/api/shorten")
    public ResponseEntity shortenUrl(@RequestBody ShortenRequest request) {
        if (request.originalUrl() == null || request.originalUrl().isBlank()) {
            return ResponseEntity.badRequest().body("Error: originalUrl is required");
        }
        
        ShortLink shortLink = service.shortenUrl(request.originalUrl());
        
        return ResponseEntity.ok(Map.of(
            "originalUrl", shortLink.getOriginalUrl(),
            "shortCode", shortLink.getShortCode(),
            "shortUrl", "http://localhost:8080/" + shortLink.getShortCode()
        ));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity redirectToOriginal(
            @PathVariable String shortCode,
            HttpServletRequest request,
            @RequestHeader(value = "User-Agent", defaultValue = "Unknown") String userAgent) {
        
        String originalUrl = service.getOriginalUrl(shortCode);
        service.recordClick(shortCode, request.getRemoteAddr(), userAgent);
        
        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }
}