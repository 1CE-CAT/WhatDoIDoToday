package ru.maks.NauJava.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ru.maks.NauJava.dao.ReportRepository;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Report;
import ru.maks.NauJava.entity.ReportStatus;
import ru.maks.NauJava.entity.Task;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public Long createReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        reportRepository.save(report);
        return report.getId();
    }

    @Override
    public Report getReport(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    @Override
    @Async
    public void generateReportAsync(Long reportId) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isEmpty()) return;
        
        Report report = reportOptional.get();

        try {
            long startTime = System.currentTimeMillis();

            long userStartTime = System.currentTimeMillis();
            long userCount = userRepository.count();
            Thread.sleep(1000);
            long userTime = System.currentTimeMillis() - userStartTime;

            long taskStartTime = System.currentTimeMillis();
            Iterable<Task> tasks = taskRepository.findAll();
            Thread.sleep(1500);
            long taskTime = System.currentTimeMillis() - taskStartTime;

            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Отчет</title></head><body>");
            html.append("<h1>Статистика приложения To-Do List</h1>");
            html.append("<p>Количество зарегистрированных пользователей: <b>").append(userCount).append("</b></p>");
            html.append("<p>Время на подсчет пользователей: ").append(userTime).append(" мс.</p>");
            
            html.append("<p>Время на получение списка задач: ").append(taskTime).append(" мс.</p>");
            
            html.append("<h2>Список задач:</h2>");
            html.append("<table border='1'><tr><th>ID</th><th>Название</th><th>Статус</th></tr>");
            for (Task task : tasks) {
                html.append("<tr>");
                html.append("<td>").append(task.getId()).append("</td>");
                html.append("<td>").append(task.getTitle()).append("</td>");
                html.append("<td>").append(task.getStatus()).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");

            long totalTime = System.currentTimeMillis() - startTime;
            html.append("<p><b>Общее время формирования отчета: ").append(totalTime).append(" мс.</b></p>");
            html.append("</body></html>");

            report.setContent(html.toString());
            report.setStatus(ReportStatus.COMPLETED);
            reportRepository.save(report);

        } catch (Exception e) {
            report.setStatus(ReportStatus.ERROR);
            report.setContent("Ошибка при формировании отчета: " + e.getMessage());
            reportRepository.save(report);
        }
    }
}