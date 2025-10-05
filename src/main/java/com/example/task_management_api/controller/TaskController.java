package com.example.task_management_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.task_management_api.model.Task;
import com.example.task_management_api.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


// TODO: whitelabel error page for http://localhost:8080/
// TODO: test posting

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(name = "status", required = false) String status) {
        List<Task> tasks;
        if (status != null) {
            status = status.trim().toLowerCase();
        }
        if (status != null && !status.isEmpty()) {
            tasks = taskService.getTasksByStatus(status);
        } else {
            tasks = taskService.getAllTasks();
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable UUID id) {
        var task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @Valid @RequestBody TaskCreateRequest request) {
        Task createdTask = taskService.createTask(
                request.getTitle(),
                request.getAuthor(),
                request.getProject(),
                request.getStatus(),
                request.getDescription());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdTask);
    }

    // xTODO BaCh: Claude proposes a class as DTO ... think about record???

    public static class TaskCreateRequest {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Author is required")
        private String author;

        @NotBlank(message = "Project is required")
        private String project;

        @NotBlank(message = "Status is required")
        @Pattern(
                regexp = "^(pending|in-progress|completed)$",
                message = "Status must be 'pending', 'in-progress', or 'completed'.")
        String status;

        private String description = "";

        public TaskCreateRequest() {
            // xTODO BaCh: hide as private and throw?
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
