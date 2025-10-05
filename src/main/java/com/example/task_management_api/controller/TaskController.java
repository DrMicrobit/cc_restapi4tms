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
                request.title(),
                request.author(),
                request.project(),
                request.status(),
                request.description());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdTask);
    }


    public record TaskCreateRequest(
            @NotBlank(message = "Title is required") String title,
            @NotBlank(message = "Author is required") String author,
            @NotBlank(message = "Project is required") String project,
            @NotBlank(message = "Status is required") @Pattern(
                    regexp = "^(pending|in-progress|completed)$",
                    message = "Status must be 'pending', 'in-progress', or 'completed'.") String status,
            String description) {


        /**
         * Canonical constructor. Using it to catch empty descriptions at entry point
         *
         * If using jackson >=2.9, could go the Jackson annotation route at record declaration level
         * above: * "@JsonSetter(nulls = Nulls.AS_EMPTY) String description"
         */
        public TaskCreateRequest {
            // normalize null description to empty string
            if (description == null) {
                description = "";
            }
        }
    }
}
