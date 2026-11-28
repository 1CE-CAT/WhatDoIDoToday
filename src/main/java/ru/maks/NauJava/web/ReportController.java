package ru.maks.NauJava.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.maks.NauJava.entity.Report;
import ru.maks.NauJava.entity.ReportStatus;
import ru.maks.NauJava.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReport() {
        Long reportId = reportService.createReport();
        
        reportService.generateReportAsync(reportId);

        Map<String, Object> response = new HashMap<>();
        response.put("reportId", reportId);
        response.put("message", "Отчет создан. Формирование запущено в фоне.");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(@PathVariable Long id) {
        Report report = reportService.getReport(id);
        
        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        if (report.getStatus() == ReportStatus.CREATED) {
            return ResponseEntity.ok("Отчет еще формируется. Пожалуйста, подождите.");
        }

        if (report.getStatus() == ReportStatus.ERROR) {
            return ResponseEntity.internalServerError().body("Ошибка при формировании отчета. " + report.getContent());
        }

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(report.getContent());
    }
}