package ru.maks.NauJava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.maks.NauJava.dao.ReportRepository;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Report;
import ru.maks.NauJava.entity.ReportStatus;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;
import ru.maks.NauJava.service.ReportServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceUnitTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void createReport_CreatesReportWithCreatedStatus() {
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report r = invocation.getArgument(0);
            r.setId(10L);
            return r;
        });

        Long id = reportService.createReport();

        Assertions.assertEquals(10L, id);
        verify(reportRepository, times(1)).save(argThat(r -> r.getStatus() == ReportStatus.CREATED));
    }

    @Test
    void getReport_ExistingId_ReturnsReport() {
        Report report = new Report();
        report.setId(5L);
        report.setStatus(ReportStatus.COMPLETED);
        when(reportRepository.findById(5L)).thenReturn(Optional.of(report));

        Report result = reportService.getReport(5L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(5L, result.getId());
        Assertions.assertEquals(ReportStatus.COMPLETED, result.getStatus());
    }

    @Test
    void getReport_MissingId_ReturnsNull() {
        when(reportRepository.findById(404L)).thenReturn(Optional.empty());

        Assertions.assertNull(reportService.getReport(404L));
    }

    @Test
    void generateReportAsync_ReportNotFound_DoesNothing() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        reportService.generateReportAsync(99L);

        verifyNoInteractions(userRepository, taskRepository);
        verify(reportRepository, never()).save(any());
    }

    @Test
    void generateReportAsync_Success_GeneratesHtmlAndMarksCompleted() {
        Report report = new Report();
        report.setId(1L);
        report.setStatus(ReportStatus.CREATED);

        Task task = new Task();
        task.setId(100L);
        task.setTitle("Тестовая задача");
        task.setStatus(TaskStatus.NEW);

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(userRepository.count()).thenReturn(3L);
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        reportService.generateReportAsync(1L);

        Assertions.assertEquals(ReportStatus.COMPLETED, report.getStatus());
        Assertions.assertNotNull(report.getContent());
        Assertions.assertTrue(report.getContent().contains("<b>3</b>"));
        Assertions.assertTrue(report.getContent().contains("Тестовая задача"));
        Assertions.assertTrue(report.getContent().contains("NEW"));
        verify(reportRepository, times(1)).save(report);
    }

    @Test
    void generateReportAsync_ExceptionThrown_MarksErrorStatus() {
        Report report = new Report();
        report.setId(2L);
        report.setStatus(ReportStatus.CREATED);

        when(reportRepository.findById(2L)).thenReturn(Optional.of(report));
        when(userRepository.count()).thenThrow(new RuntimeException("DB недоступна"));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        reportService.generateReportAsync(2L);

        Assertions.assertEquals(ReportStatus.ERROR, report.getStatus());
        Assertions.assertNotNull(report.getContent());
        Assertions.assertTrue(report.getContent().contains("DB недоступна"));
        verify(reportRepository, times(1)).save(report);
    }
}