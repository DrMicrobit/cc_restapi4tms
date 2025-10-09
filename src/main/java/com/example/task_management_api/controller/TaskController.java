package com.example.task_management_api.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.task_management_api.model.Task;
import com.example.task_management_api.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


/**
 * REST controller for managing Tasks. Provides endpoints for creating, retrieving, and deleting
 * tasks. Supports filtering tasks by status.
 */

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Task management operations")
public class TaskController {

    /**
     * Constructor for TaskController, injecting the TaskService.
     * 
     * @param taskService The service layer for task operations.
     */

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Retrieve all tasks, optionally filtered by status.
     * 
     * @param status Optional query parameter to filter tasks by their status.
     * 
     * @return A ResponseEntity containing the list of tasks (filtered if status is provided).
     */

    @GetMapping
    @Operation(summary = "Get all tasks",
            description = "Retrieve all tasks from the system, optionally filtered by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "400", description = "Invalid status parameter")
    })
    public ResponseEntity<List<Task>> getAllTasks(
            @Parameter(description = "Filter tasks by status") @RequestParam(name = "status",
                    required = false) String status) {
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

    /**
     * Retrieve a specific task by its unique ID.
     * 
     * @param id The UUID of the task to retrieve.
     * 
     * @return A ResponseEntity containing the found task, or 404 if not found.
     */

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID",
            description = "Retrieve a specific task by its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved task"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Task> getTaskById(
            @Parameter(description = "Task ID") @PathVariable UUID id) {
        var task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Delete all tasks from the system.
     * 
     * @return A ResponseEntity with appropriate status code (204)
     */

    @DeleteMapping
    @Operation(summary = "Delete all tasks", description = "Delete all tasks from the system")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully deleted all tasks")
    })
    public ResponseEntity<Void> deleteAllTasks() {
        taskService.deleteAllTasks();
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a specific task by its unique ID.
     * 
     * @param id The UUID of the task to delete.
     * 
     * @return A ResponseEntity with appropriate status code (204)
     */

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task by ID",
            description = "Delete a specific task by its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully deleted task"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


    /**
     * Create a new task with the provided details.
     * 
     * @param request The request body containing task details.
     * 
     * @return A ResponseEntity containing the created task with status 201 (Created). If validation
     *         of the task fails (e.g. status invalid or task duplicate already exists), a 400 (Bad
     *         Request) response is returned with error details.
     */
    @PostMapping
    @Operation(summary = "Create task", description = "Create a new task with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully created task"),
            @ApiResponse(responseCode = "400", description = "Invalid task data"),
            @ApiResponse(responseCode = "409", description = "Task already exists")
    })
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

    /**
     * Update an existing task with the provided details.
     * 
     * @param id The UUID of the task to update.
     * @param request The request body containing updated task details.
     * 
     * @return A ResponseEntity containing the updated task with status 200 (OK). If validation of
     *         the task fails, a 400 (Bad Request) response is returned with error details.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update task",
            description = "Update an existing task with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated task"),
            @ApiResponse(responseCode = "400", description = "Invalid task data"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "Task ID") @PathVariable UUID id,
            @Valid @RequestBody TaskUpdateRequest request) {
        Task updatedTask = taskService.updateTask(
                id,
                request.title(),
                request.author(),
                request.project(),
                request.status(),
                request.description());

        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Get the total count of tasks in the system.
     * 
     * @return A ResponseEntity containing a map with the task count.
     */

    @GetMapping("/count")
    @Operation(summary = "Get task count",
            description = "Get the total count of tasks in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved task count")
    })
    public ResponseEntity<Map<String, Long>> getTaskCount() {
        long count = taskService.countTasks();
        return ResponseEntity.ok(Map.of("count", count));
    }


    /**
     * Check if the task repository is empty.
     * 
     * @return A ResponseEntity containing a map with a boolean indicating if the repository is
     *         empty.
     */

    @GetMapping("/isempty")
    @Operation(summary = "Check if repository is empty",
            description = "Check if the task repository is empty")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully checked repository status")
    })
    public ResponseEntity<Map<String, Boolean>> getIsEmpty() {
        boolean empty = taskService.isEmpty();
        return ResponseEntity.ok(Map.of("empty", empty));
    }


    /**
     * Populate the system with a predefined set of tasks for testing or demonstration purposes.
     * 
     * @return A ResponseEntity indicating that the tasks have been populated, with status 201
     *         (Created).
     */

    @GetMapping("/populate")
    @Operation(summary = "Populate with sample tasks",
            description = "Populate the system with a predefined set of tasks for testing or demonstration purposes")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully populated tasks")
    })
    public ResponseEntity<Map<String, Boolean>> populate() {
        taskService.createPredefinedTasks();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("populated", true));
    }

    /**
     * Record representing the request body for creating a new Task. Includes validation annotations
     * to ensure required fields are present and valid.
     * 
     * Note: description is optional and defaults to an empty string if not provided.
     * 
     * @param title Title of the task (required, non-blank).
     * @param author Author of the task (required, non-blank).
     * @param project Project associated with the task (required, non-blank).
     * @param status Status of the task (required, must be one of "pending", "in-progress",
     *        "completed"). Case insensitive, lower case will be stored.
     * @param description Description of the task (optional, defaults to empty string if null).
     */

    public record TaskCreateRequest(
            @NotBlank(message = "Title is required") String title,
            @NotBlank(message = "Author is required") String author,
            @NotBlank(message = "Project is required") String project,
            @NotBlank(message = "Status is required") @Pattern(
                    regexp = "(?i)^(pending|in-progress|completed)$",
                    message = "Status must be 'pending', 'in-progress', or 'completed'.") String status,
            String description) {


        /**
         * Canonical constructor. Using it to catch empty descriptions at entry point as well as
         * transform all strings to their stored version: lower case and trimmed for status, trimmed
         * for others. This is a design choice that would need to be discussed in a real-world.
         *
         */

        public TaskCreateRequest {
            /*
             * Note to self
             * 
             * Optional field like description:
             * 
             * If using jackson >=2.9, could go the Jackson annotation route at record declaration
             * level above: "@JsonSetter(nulls = Nulls.AS_EMPTY) String description"
             * 
             * Trimming strings:
             * 
             * Could use a custom deserializer with Jackson, but that would require a full class,
             * not a record. Or use a mix-in. But that seems overkill for this. So doing it here in
             * the constructor.
             * 
             * Could use Jackson’s deserialization customization (Spring Boot). But that would be
             * more complex and less explicit.
             * 
             * In case the challenge text .....................................................
             * status (string, required, can be "pending" (default), "in-progress", or "completed")
             * ... needs to be read as "default to 'pending' if not provided", then this can be
             * addressed here.
             */


            // normalize inputs

            status = status.trim().toLowerCase();

            if (description == null) {
                description = "";
            } else {
                description = description.trim();
            }

            title = title.trim();
            author = author.trim();
            project = project.trim();
        }
    }

    /**
     * Record representing the request body for updating a Task. Includes validation annotations to
     * ensure required fields are present and valid.
     * 
     * Note: description is optional and defaults to an empty string if not provided.
     * 
     * @param title Title of the task (required, non-blank).
     * @param author Author of the task (required, non-blank).
     * @param project Project associated with the task (required, non-blank).
     * @param status Status of the task (required, must be one of "pending", "in-progress",
     *        "completed"). Case insensitive, lower case will be stored.
     * @param description Description of the task (optional, defaults to empty string if null).
     */

    public record TaskUpdateRequest(
            @NotBlank(message = "Title is required") String title,
            @NotBlank(message = "Author is required") String author,
            @NotBlank(message = "Project is required") String project,
            @NotBlank(message = "Status is required") @Pattern(
                    regexp = "(?i)^(pending|in-progress|completed)$",
                    message = "Status must be 'pending', 'in-progress', or 'completed'.") String status,
            String description) {

        /**
         * Canonical constructor. Using it to catch empty descriptions at entry point as well as
         * transform all strings to their stored version: lower case and trimmed for status, trimmed
         * for others. This is a design choice that would need to be discussed in a real-world.
         *
         */

        public TaskUpdateRequest {
            /*
             * Note to self
             * 
             * Optional field like description:
             * 
             * If using jackson >=2.9, could go the Jackson annotation route at record declaration
             * level above: "@JsonSetter(nulls = Nulls.AS_EMPTY) String description"
             * 
             * Trimming strings:
             * 
             * Could use a custom deserializer with Jackson, but that would require a full class,
             * not a record. Or use a mix-in. But that seems overkill for this. So doing it here in
             * the constructor.
             * 
             * Could use Jackson’s deserialization customization (Spring Boot). But that would be
             * more complex and less explicit.
             * 
             * In case the challenge text .....................................................
             * status (string, required, can be "pending" (default), "in-progress", or "completed")
             * ... needs to be read as "default to 'pending' if not provided", then this can be
             * addressed here.
             */


            // normalize inputs

            status = status.trim().toLowerCase();

            if (description == null) {
                description = "";
            } else {
                description = description.trim();
            }

            title = title.trim();
            author = author.trim();
            project = project.trim();
        }
    }

    // ------------------------------------------------------------------------
    // Private section from here on
    // ------------------------------------------------------------------------


    private final TaskService taskService;

}
