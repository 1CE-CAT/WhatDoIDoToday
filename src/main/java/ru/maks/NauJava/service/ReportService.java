package ru.maks.NauJava.service;

import ru.maks.NauJava.entity.Report;

public interface ReportService {
    Long createReport();
    Report getReport(Long id);
    void generateReportAsync(Long reportId);
}