package com.example.finalproject.domain.report.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "REPORT")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_PK")
    private Long id;

    @Column(nullable = false)
    private String corpName;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private String reportUrl;
}
