package ru.maks.NauJava.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ReportServiceImpl(ReportRepository reportRepository,
                             UserRepository userRepository,
                             TaskRepository taskRepository) {
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
    public void generateReportAsync(Long reportId) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isEmpty()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            Report report = reportRepository.findById(reportId).orElse(null);
            if (report == null) {
                return;
            }

            try {
                long startTime = System.currentTimeMillis();

                AtomicLong userCount = new AtomicLong();
                AtomicLong userTime = new AtomicLong();
                AtomicReference<Throwable> userError = new AtomicReference<>();

                List<Task> tasks = new ArrayList<>();
                AtomicLong taskTime = new AtomicLong();
                AtomicReference<Throwable> taskError = new AtomicReference<>();

                Thread userThread = new Thread(() -> {
                    long userStart = System.currentTimeMillis();
                    try {
                        long count = userRepository.count();
                        userCount.set(count);
                    } catch (Throwable ex) {
                        userError.set(ex);
                    } finally {
                        userTime.set(System.currentTimeMillis() - userStart);
                    }
                }, "report-user-count-thread");

                Thread taskThread = new Thread(() -> {
                    long taskStart = System.currentTimeMillis();
                    try {
                        Iterable<Task> foundTasks = taskRepository.findAll();
                        List<Task> materialized = new ArrayList<>();
                        foundTasks.forEach(materialized::add);
                        synchronized (tasks) {
                            tasks.addAll(materialized);
                        }
                    } catch (Throwable ex) {
                        taskError.set(ex);
                    } finally {
                        taskTime.set(System.currentTimeMillis() - taskStart);
                    }
                }, "report-tasks-thread");

                userThread.start();
                taskThread.start();

                userThread.join();
                taskThread.join();

                if (userError.get() != null) {
                    throw new RuntimeException("Ошибка при подсчёте пользователей", userError.get());
                }
                if (taskError.get() != null) {
                    throw new RuntimeException("Ошибка при получении списка задач", taskError.get());
                }

                long totalTime = System.currentTimeMillis() - startTime;

                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Отчет</title></head><body>");
                html.append("<h1>Статистика приложения To-Do List</h1>");
                html.append("<p>Количество зарегистрированных пользователей: <b>").append(userCount.get()).append("</b></p>");
                html.append("<p>Время на подсчет пользователей: ").append(userTime.get()).append(" мс.</p>");
                html.append("<p>Время на получение списка задач: ").append(taskTime.get()).append(" мс.</p>");
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
                html.append("<p><b>Общее время формирования отчета: ").append(totalTime).append(" мс.</b></p>");
                html.append("</body></html>");

                report.setContent(html.toString());
                report.setStatus(ReportStatus.COMPLETED);
                reportRepository.save(report);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                report.setStatus(ReportStatus.ERROR);
                report.setContent("Ошибка при формировании отчета: поток был прерван");
                reportRepository.save(report);
            } catch (Exception e) {
                report.setStatus(ReportStatus.ERROR);
                report.setContent("Ошибка при формировании отчета: " + e.getMessage());
                reportRepository.save(report);
            }
        });
    }
}