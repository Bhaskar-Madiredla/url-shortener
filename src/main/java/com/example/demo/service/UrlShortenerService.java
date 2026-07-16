package com.example.demo.service;

import com.example.demo.model.ShortLink;
import com.example.demo.model.ClickAnalytics;
import com.example.demo.repository.ShortLinkRepository;
import com.example.demo.repository.ClickAnalyticsRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UrlShortenerService {

    private final ShortLinkRepository repository;
    private final ClickAnalyticsRepository analyticsRepository;
    
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public UrlShortenerService(ShortLinkRepository repository, ClickAnalyticsRepository analyticsRepository) {
        this.repository = repository;
        this.analyticsRepository = analyticsRepository;
    }

    public ShortLink shortenUrl(String originalUrl) {
        String shortCode = generateUniqueCode();
        ShortLink link = new ShortLink();
        link.setOriginalUrl(originalUrl);
        link.setShortCode(shortCode);
        return repository.save(link); 
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomString();
        } while (repository.findByShortCode(code).isPresent());
        return code;
    }

    private String generateRandomString() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            sb.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
    
    @Cacheable(value = "shortlinks", key = "#shortCode")
    public String getOriginalUrl(String shortCode) {
        System.out.println("Cache miss! Querying PostgreSQL for code: " + shortCode);
        return repository.findByShortCode(shortCode)
                .map(ShortLink::getOriginalUrl)
                .orElseThrow(() -> new RuntimeException("Link not found!"));
    }

    @Async
    public void recordClick(String shortCode, String ip, String userAgent) {
        repository.findByShortCode(shortCode).ifPresent(link -> {
            ClickAnalytics analytics = new ClickAnalytics();
            analytics.setShortLink(link);
            analytics.setIpAddress(ip != null ? ip : "Unknown IP");
            analytics.setUserAgent(userAgent != null ? userAgent : "Unknown Browser");
            
            analyticsRepository.save(analytics);
            System.out.println("Analytics saved in background for: " + shortCode);
        });
    }
}