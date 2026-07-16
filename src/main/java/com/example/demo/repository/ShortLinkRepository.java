package com.example.demo.repository;

import com.example.demo.model.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {
    // Spring Boot reads this method name and automatically writes: 
    // SELECT * FROM short_links WHERE short_code = ?
    Optional<ShortLink> findByShortCode(String shortCode);
}