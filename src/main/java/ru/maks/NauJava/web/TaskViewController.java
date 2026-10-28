package ru.maks.NauJava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.transaction.Transactional;
import ru.maks.NauJava.dao.TaskRepository;

@Controller
@RequestMapping("/view")
public class TaskViewController {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskViewController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/task")
    @Transactional
    public String taskList(Model model) {
        model.addAttribute("task", taskRepository.findAll());
        return "task"; 
    }
}