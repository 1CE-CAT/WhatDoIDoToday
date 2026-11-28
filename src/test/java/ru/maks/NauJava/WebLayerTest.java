package ru.maks.NauJava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Report;
import ru.maks.NauJava.entity.ReportStatus;
import ru.maks.NauJava.entity.Role;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;
import ru.maks.NauJava.entity.User;
import ru.maks.NauJava.service.ReportService;
import ru.maks.NauJava.web.GlobalExceptionHandler;
import ru.maks.NauJava.web.LoginController;
import ru.maks.NauJava.web.RegistrationController;
import ru.maks.NauJava.web.ReportController;
import ru.maks.NauJava.web.TaskRestController;
import ru.maks.NauJava.web.TaskViewController;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WebLayerTest {

    @Mock
    private ReportService reportService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new ReportController(reportService),
                        new TaskRestController(taskRepository),
                        new TaskViewController(taskRepository),
                        new RegistrationController(userRepository, passwordEncoder),
                        new LoginController()
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void createReport_StartsAsyncGenerationAndReturnsId() throws Exception {
        when(reportService.createReport()).thenReturn(42L);

        mockMvc.perform(post("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(42));

        verify(reportService, times(1)).createReport();
        verify(reportService, times(1)).generateReportAsync(42L);
    }

    @Test
    void getReport_NotFound_Returns404() throws Exception {
        when(reportService.getReport(999L)).thenReturn(null);

        mockMvc.perform(get("/api/reports/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReport_InProgress_ReturnsWaitingMessage() throws Exception {
        Report report = new Report();
        report.setId(1L);
        report.setStatus(ReportStatus.CREATED);
        when(reportService.getReport(1L)).thenReturn(report);

        mockMvc.perform(get("/api/reports/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Отчет еще формируется. Пожалуйста, подождите."));
    }

    @Test
    void getReport_ErrorStatus_Returns500() throws Exception {
        Report report = new Report();
        report.setId(2L);
        report.setStatus(ReportStatus.ERROR);
        report.setContent("Сбой");
        when(reportService.getReport(2L)).thenReturn(report);

        mockMvc.perform(get("/api/reports/2"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getReport_Completed_ReturnsHtml() throws Exception {
        Report report = new Report();
        report.setId(3L);
        report.setStatus(ReportStatus.COMPLETED);
        report.setContent("<h1>Отчет</h1>");
        when(reportService.getReport(3L)).thenReturn(report);

        mockMvc.perform(get("/api/reports/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string("<h1>Отчет</h1>"));
    }

    @Test
    void searchByStatusAndDate_ReturnsMatchingTasks() throws Exception {
        Task task = new Task();
        task.setId(7L);
        task.setTitle("Срочная задача");
        task.setStatus(TaskStatus.NEW);

        LocalDateTime deadline = LocalDateTime.of(2026, 12, 1, 10, 0);
        when(taskRepository.findByStatusAndDeadlineBefore(TaskStatus.NEW, deadline))
                .thenReturn(List.of(task));

        mockMvc.perform(get("/api/task/search-by-status")
                        .param("status", "NEW")
                        .param("date", "2026-12-01T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Срочная задача"));
    }

    @Test
    void searchByUser_ThrowsIllegalArgument_HandledAs400() throws Exception {
        when(taskRepository.findByUserName("bad"))
                .thenThrow(new IllegalArgumentException("Некорректное имя"));

        mockMvc.perform(get("/api/task/search-by-user").param("userName", "bad"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка в запросе"))
                .andExpect(jsonPath("$.message").value("Некорректное имя"));
    }

    @Test
    void searchByUser_UnexpectedException_HandledAs500() throws Exception {
        when(taskRepository.findByUserName("boom"))
                .thenThrow(new RuntimeException("Неожиданный сбой"));

        mockMvc.perform(get("/api/task/search-by-user").param("userName", "boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Внутренняя ошибка сервера"));
    }

    @Test
    void viewTasks_PopulatesModelAndReturnsTaskTemplate() throws Exception {
        when(taskRepository.findAll()).thenReturn(List.of(new Task()));

        mockMvc.perform(get("/view/task"))
                .andExpect(status().isOk())
                .andExpect(view().name("task"))
                .andExpect(model().attributeExists("task"));
    }

    @Test
    void registrationGetAndLoginGet_ReturnExpectedViews() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"));

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void registrationPost_NewUser_SavesWithEncodedPasswordAndRedirects() throws Exception {
        when(userRepository.existsByName("newuser")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encodedSecret");

        mockMvc.perform(post("/registration")
                        .param("name", "newuser")
                        .param("email", "new@example.com")
                        .param("password", "secret"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userRepository, times(1)).save(argThat((User u) ->
                "newuser".equals(u.getName()) &&
                "encodedSecret".equals(u.getPassword()) &&
                u.getRole() == Role.USER
        ));
    }

    @Test
    void registrationPost_DuplicateUser_ReturnsFormWithMessage() throws Exception {
        when(userRepository.existsByName("admin")).thenReturn(true);

        mockMvc.perform(post("/registration")
                        .param("name", "admin")
                        .param("password", "secret"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("message", "Пользователь с таким именем уже существует!"));

        verify(userRepository, never()).save(any());
    }
}