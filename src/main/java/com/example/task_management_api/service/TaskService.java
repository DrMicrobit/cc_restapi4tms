package com.example.task_management_api.service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.task_management_api.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import com.example.task_management_api.model.Task;


// xTODO: guarantees and docs
// TODO: probably populate with initial entries from here
// TODO: Claude inserted HTTP error code exceptions ... nope, that's not separation!
// Clean this up later

@Service
public class TaskService {
    private final TaskRepository taskRepository;


    // VSCode says: Unnecessary `@Autowired`
    // annotationvscode-spring-boot(JAVA_AUTOWIRED_CONSTRUCTOR)
    // xTODO BaCh: read up above

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void initializeTasks() {
        if (taskRepository.isEmpty()) {
            createPredefinedTasks();
        }
    }


    // Create
    public Task createTask(String title, String author, String project, String status,
            String description) {
        // duplicate check: author / title
        if (taskRepository.existsByTitleAndAuthor(title, author)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A task with same title and author ('" + title + "', '" + author
                            + "') already exists.");
        }
        // TODO: own func to make sure UUID is unique
        var uuid = UUID.randomUUID();
        var task = new Task(uuid, title, author, project, status, description);
        return taskRepository.create(task);
    }

    // Read

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByStatus(String status) {
        if (!isValidStatus(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status '" + status
                            + "'. Must be one of: " + validStatus);
        }
        return taskRepository.findByStatus(status);
    }

    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task not found. Id: " + id));
    }

    // Update
    // nothing atm

    // Delete
    // nothing atm

    // ? Eventually special queries empty and count


    // ------------------------------------------------------------------------
    // Private section from here on
    // ------------------------------------------------------------------------

    /**
     * As not using enoum, this set of strings to bundle all valid status
     */
    // xTODO: is there no "simpler" way to declare a StringSet at compile time?
    private final Set<String> validStatus = Collections.unmodifiableSet(
            new HashSet<>(Set.of("pending", "in-progress", "completed")));

    boolean isValidStatus(String status) {
        return status != null && validStatus.contains(status);
    }


    private void createPredefinedTasks() {
        UUID taskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        ZonedDateTime tCreated = ZonedDateTime.parse("2025-09-29T13:23:16Z");
        ZonedDateTime tUpdated = ZonedDateTime.parse("2025-09-29T13:23:16Z");
        Task task = new Task(
                taskId,
                "Implement User Authentication",
                "Alice Johnson",
                "Authentication System", // Missing in Challenge Instructions
                "Create a secure user authentication system using JWT.",
                "pending",
                tCreated,
                tUpdated);
        taskRepository.create(task);


        taskId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        tCreated = ZonedDateTime.parse("2025-09-28T10:15:30Z");
        tUpdated = ZonedDateTime.parse("2025-09-29T09:00:00Z");
        task = new Task(
                taskId,
                "Design Database Scheme",
                "Bob Smith",
                "Database Design", // Missing in Challenge Instructions
                "Draft the database schema for the project",
                "pending",
                tCreated,
                tUpdated);
        taskRepository.create(task);

        // xTODO: don't like println, read up on how to log with spring
        System.out.println("Initialised " + taskRepository.count() + " predfined tasks.");
    }

}
