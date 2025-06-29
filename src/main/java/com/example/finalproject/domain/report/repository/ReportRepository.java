package com.example.finalproject.domain.report.repository;

import com.example.finalproject.domain.report.entity.ReportEntity;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    Optional<ReportEntity> findByCorpName(String corpName);
}
