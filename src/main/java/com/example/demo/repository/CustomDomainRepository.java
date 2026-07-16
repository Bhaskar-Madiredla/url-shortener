package com.example.demo.repository;

import com.example.demo.model.CustomDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomDomainRepository extends JpaRepository<CustomDomain, Long> {
    Optional<CustomDomain> findByDomainName(String domainName);
}