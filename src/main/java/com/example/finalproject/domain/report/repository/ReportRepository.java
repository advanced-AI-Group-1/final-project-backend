package com.example.finalproject.domain.report.repository;

import com.example.finalproject.domain.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    Optional<ReportEntity> findByCorpName(String corpName);
}
