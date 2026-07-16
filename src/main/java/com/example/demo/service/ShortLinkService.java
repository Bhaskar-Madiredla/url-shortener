package com.example.demo.service;

import com.example.demo.model.ShortLink;
import com.example.demo.controller.MetricsController;

import com.example.demo.repository.ShortLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShortLinkService {

    @Autowired
    private ShortLinkRepository shortLinkRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public ShortLink getOriginalUrl(String shortCode) {
        long startTime = System.currentTimeMillis();
        boolean cacheHit = false;

        // 1. Attempt to fetch from Redis
        ShortLink link = (ShortLink) redisTemplate.opsForValue().get(shortCode);

        if (link != null) {
            cacheHit = true;
        } else {
            // 2. Cache miss: Fetch from PostgreSQL
            // .orElse(null) extracts the object if it exists, otherwise returns null
            link = shortLinkRepository.findByShortCode(shortCode).orElse(null);
            
            // 3. Update cache if found
            if (link != null) {
                redisTemplate.opsForValue().set(shortCode, link);
            }
        }

        // 4. Calculate latency and report to MetricsController
        long duration = System.currentTimeMillis() - startTime;
        MetricsController.recordRequest(duration, cacheHit);

        return link;
    }
}