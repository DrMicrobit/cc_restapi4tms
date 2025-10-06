package com.example.task_management_api.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.example.task_management_api.model.Task;

/**
 * In-memory implementation of TaskRepository using a ConcurrentHashMap for thread-safe storage.
 * Suitable for testing and development purposes.
 */

@Repository
public class TaskRepositoryInMemory implements TaskRepository {

    // Create

    /**
     * Create a new Task and store it in the repository.
     *
     * @apiNote The Task's ID must be unique; no check is performed here. If not unique, it will
     *          overwrite existing Task and thus effectively be an update.
     *
     * @param task The Task to be created.
     * 
     * @return The created Task.
     */
    @Override
    public Task create(Task task) {
        tasks.put(task.id(), task);
        return task;
    }

    // Read

    /**
     * Retrieve all Tasks from the repository.
     * 
     * @return A list of all Tasks, empty list if none.
     */

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Find a Task by its unique ID.
     * 
     * @param id The UUID of the Task to find.
     * 
     * @return An Optional containing the found Task, or empty if not found.
     */

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    /**
     * Find all Tasks with the specified status.
     * 
     * @param status The status to filter Tasks by.
     * 
     * @return A list of Tasks matching the status, empty list if none found.
     */

    @Override
    public List<Task> findByStatus(String status) {
        return tasks.values().stream()
                .filter(task -> status.equals(task.status()))
                .toList();
    }

    // Update
    // nothing atm

    // Delete

    /**
     * Clear all Tasks from the repository.
     */

    @Override
    public void clear() {
        tasks.clear();
    }

    /**
     * Delete a Task by its unique ID.
     * 
     * @param id The UUID of the Task to delete.
     */
    @Override
    public void deleteById(UUID id) {
        tasks.remove(id);
    }

    // Query

    /**
     * Check if a Task exists with the given title and author.
     * 
     * @implNote does not handle null values for title or author, assumes non-null inputs.
     * 
     * @param title The title of the Task.
     * @param author The author of the Task.
     * 
     * @return true if a matching Task exists, false otherwise.
     */

    public boolean existsByTitleAndAuthor(String title, String author) {
        return tasks.values().stream().anyMatch(
                task -> title.equals(task.title()) && author.equals(task.author()));
    }


    // Container-like convenience functions

    /**
     * Get the count of Tasks in the repository.
     * 
     * @return The number of Tasks.
     */
    @Override
    public long count() {
        return tasks.size();
    }

    /**
     * Check if the repository is empty.
     * 
     * @return true if no Tasks are present, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return tasks.isEmpty();
    }


    // ------------------------------------------------------------------------
    // Private section from here on
    // ------------------------------------------------------------------------

    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();
}
