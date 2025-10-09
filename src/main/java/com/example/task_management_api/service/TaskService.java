package com.example.task_management_api.service;

import java.time.Instant;
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


/**
 * Service layer for task management. Implements business logic and interacts with the repository
 * layer.
 * <p>
 * This service provides methods to create, read, and delete tasks, as well as to perform specific
 * queries such as counting tasks or checking if the repository is empty. No update functionality is
 * implemented at this time.
 * </p>
 * <p>
 * Error handling is implemented using {@link ResponseStatusException} to provide appropriate HTTP
 * status codes for various error scenarios. This is a design choice for simplicity in this example,
 * but in a production application, a more robust error handling strategy and separation of concerns
 * would be advisable.
 * </p>
 * <p>
 * The service also includes an initialization method to populate the repository with predefined
 * tasks at startup, if the repository is empty. This is primarily for testing and demonstration
 * purposes.
 * </p>
 */



@Service
public class TaskService {
    /*
     * Constructor
     */
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * At startup, initializes the repository with two predefined tasks, if repository is empty.
     * Used for testing and demo purposes.
     */
    @PostConstruct
    public void initializeTasks() {
        if (taskRepository.isEmpty()) {
            createPredefinedTasks();
        }
    }


    // Create

    /**
     * Creates a new task in repository. If a task with same title and author already exists, throws
     * 409.
     * 
     * @apiNote: Broken separation of concerns, as throwing HTTP exception from service layer.
     * 
     * @param title the title of the task
     * @param author the author of the task
     * @param project the project the task belongs to
     * @param status the status of the task, must be one of "pending", "in-progress", "completed"
     * @param description the description of the task
     * @return the created task
     * @throws ResponseStatusException with status 409 if a task with same title and author already
     *         exists
     * @throws ResponseStatusException with status 400 if status is invalid
     */
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

    /**
     * Get all tasks in repository.
     * 
     * @return list of all tasks, possibly empty
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Get all tasks with given status. If status is invalid, throws 400.
     * 
     * @apiNote: Broken separation of concerns, as throwing HTTP exception from service layer.
     * 
     * @param status the status to filter by
     * @return list of tasks with given status, possibly empty
     * @throws ResponseStatusException with status 400 if status is invalid
     */
    public List<Task> getTasksByStatus(String status) {
        if (!isValidStatus(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status '" + status
                            + "'. Must be one of: " + validStatus);
        }
        return taskRepository.findByStatus(status);
    }

    /*
     * Get a task by its UUID. If no such task exists, throws 404.
     * 
     * @apiNote: Broken separation of concerns, as throwing HTTP exception from service layer.
     * 
     * @param id the UUID of the task to retrieve
     * 
     * @return the task with the given UUID if it exists
     * 
     * @throws ResponseStatusException with status 404 if no such task exists
     */
    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task not found. Id: " + id));
    }

    // Update

    /**
     * Updates an existing task in repository. If no task with given ID exists, throws 404.
     * 
     * @param id the UUID of the task to update
     * @param title the title of the task
     * @param author the author of the task
     * @param project the project the task belongs to
     * @param status the status of the task, must be one of "pending", "in-progress", "completed"
     * @param description the description of the task
     * @return the updated task
     * @throws ResponseStatusException with status 404 if no task with given ID exists
     * @throws ResponseStatusException with status 400 if status is invalid
     */
    public Task updateTask(UUID id, String title, String author, String project, String status,
            String description) {
        // Check if task exists
        var existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task not found. Id: " + id));

        // Check for duplicate if title and author are changed
        if (!existingTask.title().equals(title) || !existingTask.author().equals(author)) {
            if (taskRepository.existsByTitleAndAuthor(title, author)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A task with same title and author ('" + title + "', '" + author
                                + "') already exists.");
            }
        }

        // Update task with new values
        var updatedTask = new Task(id, title, author, project, status, description,
                existingTask.createdAt(), Instant.now());

        return taskRepository.create(updatedTask);
    }

    // Delete

    /**
     * Deletes all tasks from repository. If repository is already empty, does nothing.
     */
    public void deleteAllTasks() {
        taskRepository.clear();
    }

    /**
     * Deletes a task by its UUID. If no such task exists, does nothing.
     * 
     * @param id the UUID of the task to delete
     */
    public void deleteTaskById(UUID id) {
        taskRepository.deleteById(id);;
    }

    // Special queries empty and count


    /**
     * Counts the number of tasks in repository.
     * 
     * @return the number of tasks in repository
     */
    public long countTasks() {
        return taskRepository.count();
    }

    /**
     * Checks if repository is empty.
     * 
     * @apiNote Probably not canon for Spring containers, but cheap enough to implement and could be
     *          important later in production to alleviate stress on a real DB.
     * 
     * @return true if repository is empty, false if not
     */
    public boolean isEmpty() {
        return taskRepository.isEmpty();
    }



    /**
     * Creates two predefined tasks in repository, if not already present. Used for testing and demo
     * purposes.
     * 
     * @implNote Careful: Barebone task creation, not going through validators!
     * @implNote The UUIDs and timestamps are hardcoded, so that tests can rely on them. If the
     *           tasks are already present (e.g. from a previous run), does nothing. If only one of
     *           the two tasks is present, still creates both
     */
    public void createPredefinedTasks() {
        UUID taskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Instant tCreated = Instant.parse("2025-09-29T13:23:16Z");
        Instant tUpdated = Instant.parse("2025-09-29T13:23:16Z");
        Task task = new Task(
                taskId,
                "Implement User Authentication",
                "Alice Johnson",
                "Authentication System", // Missing in Challenge Instructions
                "pending", // NOSONAR // no, I won't define a constant for that
                "Create a secure user authentication system using JWT.",
                tCreated,
                tUpdated);
        taskRepository.create(task);


        taskId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        tCreated = Instant.parse("2025-09-28T10:15:30Z");
        tUpdated = Instant.parse("2025-09-29T09:00:00Z");
        task = new Task(
                taskId,
                "Design Database Scheme",
                "Bob Smith",
                "Database Design", // Missing in Challenge Instructions
                "in-progress", // NOSONAR // no, I won't define a constant for that
                "Draft the database schema for the project",
                tCreated,
                tUpdated);
        taskRepository.create(task);

        /*
         * tested how things react to nulls. Not pretty. Especially if title or author or is null,
         * createTask will fail add an a new task, as the repository query to check for duplicates
         * fails.
         *
         * Decision: atm rely on defense in controller, not here or in repository / model.
         *
         * taskRepository.create(new Task( UUID.randomUUID(), "Testing programs right", //
         * "A. Nonymous", null, "RESTful API", "in-progress", "What can I say ...?"));
         * 
         */

    }



    // ------------------------------------------------------------------------
    // Private section from here on
    // ------------------------------------------------------------------------

    // Repository instance, injected by Spring. Later could be interface to allow
    // different implementations and injection of mock repository for testing.
    private final TaskRepository taskRepository;


    /**
     * As not using enum, this set of strings to bundle all valid status
     */
    // xTODO: is there no "simpler" way to declare a StringSet at compile time?
    private final Set<String> validStatus = Collections.unmodifiableSet(
            new HashSet<>(Set.of("pending", "in-progress", "completed"))); // NOSONAR

    boolean isValidStatus(String status) {
        return status != null && validStatus.contains(status);
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
