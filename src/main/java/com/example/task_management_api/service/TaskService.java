package com.example.task_management_api.service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.task_management_api.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import com.example.task_management_api.model.Task;


// xTODO: guarantees and docs


@Service
public class TaskService {
    private final TaskRepository taskRepository;


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
        var uuid = getNewUuid();
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
    public void deleteAllTasks() {
        taskRepository.clear();
    }

    public void deleteTaskById(UUID id) {
        taskRepository.deleteById(id);;
    }

    // ? Eventually special queries empty and count


    // ------------------------------------------------------------------------
    // Private section from here on
    // ------------------------------------------------------------------------

    /**
     * As not using enum, this set of strings to bundle all valid status
     */
    // xTODO: is there no "simpler" way to declare a StringSet at compile time?
    private final Set<String> validStatus = Collections.unmodifiableSet(
            new HashSet<>(Set.of("pending", "in-progress", "completed")));

    boolean isValidStatus(String status) {
        return status != null && validStatus.contains(status);
    }


    /**
     * Add two pre-defined tasks to the repository.
     *
     * Careful: Barebone task creation, not going through validator.
     */
    private void createPredefinedTasks() {
        UUID taskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        ZonedDateTime tCreated = ZonedDateTime.parse("2025-09-29T13:23:16Z");
        ZonedDateTime tUpdated = ZonedDateTime.parse("2025-09-29T13:23:16Z");
        Task task = new Task(
                taskId,
                "Implement User Authentication",
                "Alice Johnson",
                "Authentication System", // Missing in Challenge Instructions
                "pending",
                "Create a secure user authentication system using JWT.",
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
                "pending",
                "Draft the database schema for the project",
                tCreated,
                tUpdated);
        taskRepository.create(task);

        // xTODO: don't like println, read up on how to log with spring
        System.out.println("Initialised " + taskRepository.count() + " predefined tasks.");
    }


    /**
     * Creates a new UUID suitable for inserting a new task into repository.
     *
     * @apiNote Caution: not production level ready. Tries up to 1000 UUIDs, if all already present,
     *          throws.
     *
     * @return UUID
     * @throws IllegalStateException if no proper UUID could be found.
     */
    private UUID getNewUuid() {
        boolean haveUuid = false;
        UUID newId = UUID.randomUUID();
        for (int i = 0; i < 1000 && !haveUuid; ++i) {
            var idInRepository = taskRepository.findById(newId);
            if (!idInRepository.isPresent()) {
                haveUuid = true;
            }
            newId = UUID.randomUUID();
        }
        if (!haveUuid) {
            // in "real" production code: exception handler for whole project
            // together with logger, mail, etc. to alert ppl. Would probably have
            // own exception class.
            String emsg =
                    "Tried 1000 times to create a UUID not already present in repository. Failed?";
            System.err.println(emsg);
            throw new IllegalStateException(emsg);
        }
        return newId;
    }
}
